package com.github.euler.tika;

import java.net.URL;
import java.util.List;

import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.ConfigUtil;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class DefaultParseContextFactoryConfigConverter extends AbstractParseContextFactory {

    @Override
    public String configType() {
        return "default";
    }

    @Override
    public ParseContextFactory convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        boolean skipOcr = config.getBoolean("ocr.skip-ocr");
        List<String> includeMimetypeRegex = getList(config, "ocr.include-mime-type-regex");
        List<String> excludeMimetypeRegex = getList(config, "ocr.exclude-mime-type-regex");

        TesseractOCRConfig ocrConfig = new TesseractOCRConfig();
        ConfigUtil.set(ocrConfig, config.getConfig("tesseract"));

        PDFParserConfig pdfParserConfig = new PDFParserConfig();
        ConfigUtil.set(pdfParserConfig, config.getConfig("pdf"));

        return new DefaultParseContextFactory(skipOcr, includeMimetypeRegex, excludeMimetypeRegex, ocrConfig, pdfParserConfig);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    private List<String> getList(Config config, String path) {
        try {
            return config.getStringList(path);
        } catch (ConfigException.WrongType e) {
            return List.of(config.getString(path));
        }
    }

    protected Config getDefaultConfig() {
        URL resource = DefaultParseContextFactoryConfigConverter.class.getClassLoader().getResource("defaultparsecontext.conf");
        return ConfigFactory.parseURL(resource);
    }

}
