package com.github.euler.tika;

import java.net.URL;
import java.util.List;

import org.apache.tika.parser.ocr.TesseractOCRConfig;

import com.github.euler.configuration.ConfigContext;
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
        ocrConfig.setLanguage(config.getString("ocr.language"));
        ocrConfig.setEnableImageProcessing(config.getBoolean("ocr.enable-image-processing") ? 1 : 0);
        ocrConfig.setFilter(config.getString("ocr.filter"));
        ocrConfig.setMinFileSizeToOcr(config.getLong("ocr.min-file-size-to-ocr"));
        ocrConfig.setMaxFileSizeToOcr(config.getLong("ocr.max-file-size-to-ocr"));

        return new DefaultParseContextFactory(skipOcr, includeMimetypeRegex, excludeMimetypeRegex, ocrConfig);
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
