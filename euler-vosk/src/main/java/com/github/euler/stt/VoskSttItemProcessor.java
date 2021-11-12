package com.github.euler.stt;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URI;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vosk.Recognizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.euler.common.CommonContext;
import com.github.euler.common.StorageStrategy;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class VoskSttItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final StreamFactory sf;
    private final StorageStrategy storageStrategy;
    private final Recognizer recognizer;

    private final ObjectMapper mapper = new ObjectMapper();

    public VoskSttItemProcessor(StreamFactory sf, StorageStrategy storageStrategy, Recognizer recognizer) {
        super();
        this.sf = sf;
        this.storageStrategy = storageStrategy;
        this.recognizer = recognizer;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        URI outURI = storageStrategy.createFile(item.itemURI, ".txt");
        try (InputStream in = sf.openInputStream(item.itemURI, item.ctx); OutputStream out = sf.openOutputStream(outURI, item.ctx)) {
            processAudio(in, out);
            return ProcessingContext.builder()
                    .context(CommonContext.PARSED_CONTENT_FILE, outURI)
                    .build();
        } catch (UnsupportedAudioFileException e) {
            LOGGER.warn("An error ocurred while detecting speech for " + item.itemURI, e);
            return ProcessingContext.EMPTY;
        }
    }

    public void processAudio(InputStream in, OutputStream out) throws IOException, UnsupportedAudioFileException {
        InputStream ais = AudioSystem.getAudioInputStream(in);
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, "utf-8")));
        int nbytes;
        byte[] b = new byte[4096];
        while ((nbytes = ais.read(b)) >= 0) {
            if (recognizer.acceptWaveForm(b, nbytes)) {
                String acceptedResult = recognizer.getResult();
                String parsed = parseResult(acceptedResult);
                if (!parsed.isBlank()) {
                    writer.println(parsed);
                }
            }
        }
        String finalResult = recognizer.getFinalResult();
        String parsed = parseResult(finalResult);
        if (!parsed.isBlank()) {
            writer.println(parsed);
        }
        writer.flush();
    }

    private String parseResult(String result) throws JsonMappingException, JsonProcessingException {
        JsonNode jsonNode = this.mapper.readTree(result);
        return jsonNode.get("text").asText();
    }

}
