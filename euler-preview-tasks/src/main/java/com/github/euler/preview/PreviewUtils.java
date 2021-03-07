package com.github.euler.preview;

import java.awt.image.BufferedImageOp;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import com.typesafe.config.Config;

public class PreviewUtils {

    private PreviewUtils() {
        super();
    }

    public static ScalrConfig fromConfig(Config config) {
        Method method = config.getEnum(Method.class, "method");
        Mode mode = config.getEnum(Mode.class, "mode");
        int width = config.getInt("width");
        int height = config.getInt("height");
        BufferedImageOp[] ops = getOps(config, "ops");
        return new ScalrConfig(method, mode, width, height, ops);
    }

    private static BufferedImageOp[] getOps(Config config, String path) {
        return config.getStringList(path).stream()
                .map(o -> {
                    try {
                        BufferedImageOp op = (BufferedImageOp) Scalr.class.getField(o).get(null);
                        return op;
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                }).toArray(s -> new BufferedImageOp[s]);
    }

}
