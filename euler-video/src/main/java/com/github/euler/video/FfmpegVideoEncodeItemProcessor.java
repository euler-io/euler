package com.github.euler.video;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.StorageStrategy;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;
import com.github.kokorin.jaffree.LogLevel;
import com.github.kokorin.jaffree.ffmpeg.BaseInput;
import com.github.kokorin.jaffree.ffmpeg.BaseOutput;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;

public class FfmpegVideoEncodeItemProcessor implements ItemProcessor {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final StorageStrategy storageStrategy;
    private final String field;
    private final String[] additionalArgs;

    public FfmpegVideoEncodeItemProcessor(InputFactory inputFactory,
            OutputFactory outputFactory,
            StorageStrategy storageStrategy,
            String field,
            String... additionalArgs) {
        super();
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.storageStrategy = storageStrategy;
        this.field = field;
        this.additionalArgs = additionalArgs;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        try {
            URI outURI = storageStrategy.createFile(item.itemURI);
            encode(item.itemURI, outURI, item.ctx);
            return ProcessingContext.builder()
                    .metadata(field, outURI.toString())
                    .build();
        } catch (Throwable e) {
            LOGGER.warn("An error ocurred while creating video preview for " + item.itemURI, e);
            return ProcessingContext.EMPTY;
        }
    }

    private void encode(URI inURI, URI outURI, ProcessingContext ctx) {
        BaseInput<?> input = inputFactory.createFFmpeg(inURI, ctx);
        BaseOutput<?> output = outputFactory.create(outURI, ctx);

        FFmpeg ffmpeg = FFmpeg.atPath()
                .setLogLevel(LogLevel.QUIET)
                .setOverwriteOutput(true);

        Arrays.stream(additionalArgs).forEach(a -> ffmpeg.addArgument(a));
        ffmpeg.addInput(input).addOutput(output);
        ffmpeg.execute();
    }

}
