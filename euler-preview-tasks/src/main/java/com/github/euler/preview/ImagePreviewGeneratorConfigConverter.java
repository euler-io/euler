package com.github.euler.preview;

import java.awt.image.BufferedImageOp;
import java.net.URL;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.preview.ImagePreviewGenerator.ScalrConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ImagePreviewGeneratorConfigConverter extends AbstractPreviewGeneratorConfigConverter {

    @Override
    public String configType() {
        return "image";
    }

    @Override
    public PreviewGenerator convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());

        Method method = config.getEnum(Method.class, "method");
        Mode mode = config.getEnum(Mode.class, "mode");
        int width = config.getInt("width");
        int height = config.getInt("height");
        BufferedImageOp[] ops = getOps(config);
        ScalrConfig scalrConfig = new ScalrConfig(method, mode, width, height, ops);
        return new ImagePreviewGenerator(scalrConfig);
    }

    private BufferedImageOp[] getOps(Config config) {
        return config.getStringList("ops").stream()
                .map(o -> {
                    try {
                        BufferedImageOp op = (BufferedImageOp) Scalr.class.getField(o).get(null);
                        return op;
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(s -> new BufferedImageOp[s]);
    }

    protected Config getDefaultConfig() {
        URL resource = PreviewTaskConfigConverter.class.getClassLoader().getResource("imagepreviewgenerator.conf");
        return ConfigFactory.parseURL(resource);
    }

}
