package com.github.euler.preview;

import java.net.URL;

import org.apache.pdfbox.rendering.ImageType;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class PDFBoxPreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "pdf";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        PDFBoxPreviewGenerator.Config pdfConfig = new PDFBoxPreviewGenerator.Config();
        pdfConfig.setDpi(config.getInt("dpi"));
        pdfConfig.setInitialPage(config.getInt("initial-page"));
        pdfConfig.setMaxPages(config.getInt("max-pages"));
        pdfConfig.setImageType(config.getEnum(ImageType.class, "image-type"));
        return new PDFBoxPreviewGenerator(pdfConfig);
    }

    protected Config getDefaultConfig() {
        URL resource = PreviewTaskConfigConverter.class.getClassLoader().getResource("pdfpreviewgenerator.conf");
        return ConfigFactory.parseURL(resource);
    }

}
