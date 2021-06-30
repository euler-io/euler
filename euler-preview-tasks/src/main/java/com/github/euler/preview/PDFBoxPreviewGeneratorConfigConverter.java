package com.github.euler.preview;

import java.net.URL;

import org.apache.pdfbox.rendering.ImageType;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class PDFBoxPreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "pdf";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = getConfig(config);
        PDFBoxPreviewGenerator.Config pdfConfig = new PDFBoxPreviewGenerator.Config();
        pdfConfig.setDpi(config.getInt("dpi"));
        pdfConfig.setInitialPage(config.getInt("initial-page"));
        pdfConfig.setMaxPages(config.getInt("max-pages"));
        pdfConfig.setImageType(config.getEnum(ImageType.class, "image-type"));
        return new PDFBoxPreviewGenerator(pdfConfig);
    }

    protected Config getConfig(Config config) {
        return ConfigFactory.parseString(config.root().render(ConfigRenderOptions.concise())).withFallback(getDefaultConfig()).resolve();
    }

    protected Config getDefaultConfig() {
        URL resource = PDFBoxPreviewGeneratorConfigConverter.class.getClassLoader().getResource("pdfpreviewgenerator.conf");
        return ConfigFactory.parseURL(resource);
    }

}
