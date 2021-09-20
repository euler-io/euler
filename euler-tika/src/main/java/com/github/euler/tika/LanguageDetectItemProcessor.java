package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BoundedInputStream;
import org.apache.tika.language.detect.LanguageDetector;

import com.github.euler.common.CommonContext;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class LanguageDetectItemProcessor implements ItemProcessor {

    private final LanguageDetector langDetector;
    private final StreamFactory sf;
    private final long maxBytes;
    private final int maxLangs;
    private final String field;

    public LanguageDetectItemProcessor(LanguageDetector langDetector, StreamFactory sf, long maxBytes, int maxLangs, String field) {
        super();
        this.langDetector = langDetector;
        this.sf = sf;
        this.maxBytes = maxBytes;
        this.maxLangs = maxLangs;
        this.field = field;
    }

    public LanguageDetectItemProcessor(StreamFactory sf, long maxBytes, int maxLangs, String field) {
        this(LanguageDetector.getDefaultLanguageDetector(), sf, maxBytes, maxLangs, field);
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        URI uri = item.ctx.context(CommonContext.PARSED_CONTENT_FILE, null);
        try (InputStream in = new BoundedInputStream(sf.openInputStream(uri, item.ctx), maxBytes)) {
            String text = IOUtils.toString(in, "utf-8");
            Map<String, String> languagesScores = langDetector.detectAll(text).stream()
                    .limit(maxLangs)
                    .collect(Collectors.toMap(r -> r.getLanguage().toString(), r -> r.getConfidence().toString()));
            return ProcessingContext.builder()
                    .metadata(field, languagesScores)
                    .build();
        }
    }

}
