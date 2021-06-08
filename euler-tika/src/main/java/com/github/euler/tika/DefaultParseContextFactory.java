package com.github.euler.tika;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;

import com.github.euler.common.CommonMetadata;
import com.github.euler.core.ProcessingContext;

public class DefaultParseContextFactory implements ParseContextFactory {

    private final boolean skipOcr;
    private final List<Pattern> includeMimetypePatterns;
    private final List<Pattern> excludeMimetypePatterns;
    private final TesseractOCRConfig ocrConfig;

    public DefaultParseContextFactory(boolean skipOcr, List<String> includeMimetypeRegex, List<String> excludeMimetypeRegex, TesseractOCRConfig ocrConfig) {
        super();
        this.skipOcr = skipOcr;
        this.includeMimetypePatterns = compile(includeMimetypeRegex);
        this.excludeMimetypePatterns = compile(excludeMimetypeRegex);
        this.ocrConfig = ocrConfig;
    }

    private List<Pattern> compile(List<String> regex) {
        return regex.stream()
                .map(r -> Pattern.compile(r))
                .collect(Collectors.toList());
    }

    public DefaultParseContextFactory() {
        this(true, List.of("a^"), List.of("^a"), new TesseractOCRConfig());
    }

    @Override
    public ParseContext create(ProcessingContext ctx) {
        ParseContext parseContext = new ParseContext();
        if (!skipOcr) {
            String mimeType = ctx.metadata(CommonMetadata.MIME_TYPE, null);
            if (mimeType != null && (matches(mimeType, includeMimetypePatterns) && !matches(mimeType, excludeMimetypePatterns))) {
                PDFParserConfig pdfConfig = new PDFParserConfig();
                pdfConfig.setExtractInlineImages(true);
                parseContext.set(PDFParserConfig.class, pdfConfig);
                parseContext.set(TesseractOCRConfig.class, ocrConfig);
            }
        }
        return parseContext;
    }

    private boolean matches(String value, List<Pattern> patterns) {
        return patterns.stream()
                .anyMatch(p -> p.matcher(value).matches());
    }

}
