package com.github.euler.file;

import com.github.euler.common.AbstractStreamFactoryConfigConverter;
import com.github.euler.common.StreamFactory;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.typesafe.config.Config;

public class FileStreamFactoryConfigConverter extends AbstractStreamFactoryConfigConverter {

    @Override
    public StreamFactory convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new FileStreamFactory();
    }

    @Override
    public String configType() {
        return "file";
    }

}
