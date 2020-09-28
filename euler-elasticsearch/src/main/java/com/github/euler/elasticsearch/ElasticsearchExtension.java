package com.github.euler.elasticsearch;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class ElasticsearchExtension implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new ElasticsearchFragmentTaskConfigConverter(),
                new ElasticsearchMetadataTaskConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return Arrays.asList(new ElasticsearchSourceConfigConverter(),
                new ElasticsearchConfigClientConfigConverter(),
                new ElasticsearchContextClientConfigConverter());
    }

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new ElasticsearchClientConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Elasticsearch Extension";
    }

}
