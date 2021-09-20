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
    private final PDFParserConfig pdfParserConfig;

    private final TesseractOCRConfig skipOCRConfig;

    public DefaultParseContextFactory(boolean skipOcr, List<String> includeMimetypeRegex, List<String> excludeMimetypeRegex, TesseractOCRConfig ocrConfig,
            PDFParserConfig pdfParserConfig) {
        super();
        this.skipOcr = skipOcr;
        this.includeMimetypePatterns = compile(includeMimetypeRegex);
        this.excludeMimetypePatterns = compile(excludeMimetypeRegex);
        this.ocrConfig = ocrConfig;
        this.pdfParserConfig = pdfParserConfig;
        this.skipOCRConfig = initSkipOCRConfig();
    }

    private TesseractOCRConfig initSkipOCRConfig() {
        TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
        ocrConfig.setMaxFileSizeToOcr(0);
        ocrConfig.setTesseractPath("/tmp");
        return ocrConfig;
    }

    public DefaultParseContextFactory() {
        this(true, List.of("a^"), List.of("^a"), new TesseractOCRConfig(), new PDFParserConfig());
    }

    private List<Pattern> compile(List<String> regex) {
        return regex.stream()
                .map(r -> Pattern.compile(r))
                .collect(Collectors.toList());
    }

    @Override
    public ParseContext create(ProcessingContext ctx) {
        ParseContext parseContext = new ParseContext();
        if (!skipOcr) {
            String mimeType = ctx.metadata(CommonMetadata.MIME_TYPE, null);
            if (mimeType != null && (matches(mimeType, includeMimetypePatterns) && !matches(mimeType, excludeMimetypePatterns))) {
                parseContext.set(PDFParserConfig.class, pdfParserConfig);
                parseContext.set(TesseractOCRConfig.class, ocrConfig);
            }
        } else {
            parseContext.set(TesseractOCRConfig.class, skipOCRConfig);
        }
        return parseContext;
    }

    private boolean matches(String value, List<Pattern> patterns) {
        return patterns.stream()
                .anyMatch(p -> p.matcher(value).matches());
    }

}
