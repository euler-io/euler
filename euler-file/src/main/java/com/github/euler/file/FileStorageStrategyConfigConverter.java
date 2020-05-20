package com.github.euler.file;

import java.io.File;

import com.github.euler.common.AbstractStorageStrategyConfigConverter;
import com.github.euler.common.StorageStrategy;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class FileStorageStrategyConfigConverter extends AbstractStorageStrategyConfigConverter {

    @Override
    public StorageStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        File root = null;
        String suffix = null;
        return new FileStorageStrategy(root, suffix);
    }

    @Override
    public String configType() {
        return "file";
    }

}
