package com.github.euler.tika.metadata;

import java.net.URL;
import java.util.List;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ObjectMetadataParserConfigConverter extends DefaultMetadataParserConfigConverter {

    @Override
    public String configType() {
        return "object";
    }

    @Override
    protected MetadataParser create(Config config, String includePattern, String excludePattern, List<MetadataFieldParser> fieldParsers) {
        String field = config.getString("field");
        return new ObjectMetadataParser(field, includePattern, excludePattern, fieldParsers);
    }

    protected Config getDefaultConfig() {
        URL resource = ObjectMetadataParserConfigConverter.class.getClassLoader().getResource("objectmetadataparser.conf");
        return ConfigFactory.parseURL(resource);
    }

}
