package com.github.euler.elasticsearch;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;

public class ElasticsearchExtension implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new ElasticsearchFragmentTaskConfigConverter(),
                new ElasticsearchMetadataTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Elasticsearch Extension";
    }

}