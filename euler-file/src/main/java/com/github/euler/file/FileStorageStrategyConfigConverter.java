package com.github.euler.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class FileStorageStrategyConfigConverter extends AbstractStorageStrategyConfigConverter {

    public static final String TEMPORARY_ROOT_FOLDER = "temporary-folder";

    @Override
    public StorageStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        File root = getRoot(config);
        String suffix = config.getString("suffix");
        return new FileStorageStrategy(root, suffix);
    }

    private File getRoot(Config config) {
        String root = config.getString("root");
        if (root.equals(TEMPORARY_ROOT_FOLDER)) {
            try {
                return Files.createTempDirectory("euler").toFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return new File(root);
        }
    }

    @Override
    public String configType() {
        return "file";
    }

}
