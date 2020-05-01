package com.github.euler.file;

import com.github.euler.common.StreamFactory;
import com.github.euler.common.StreamFactoryCreator;
import com.github.euler.config.ConfigContext;
import com.typesafe.config.Config;

public class FileStreamFactoryCreator implements StreamFactoryCreator {

    @Override
    public String type() {
        return "file";
    }

    @Override
    public StreamFactory create(Config config, ConfigContext ctx) {
        return new FileStreamFactory();
    }

}
