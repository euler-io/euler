package com.github.euler.tika;

import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;
import com.github.euler.tika.embedded.DefaultEmbeddedNamingStrategyConfigConverter;
import com.github.euler.tika.embedded.MimetypeEmbeddedNamingStrategyConfigConverter;
import com.github.euler.tika.embedded.RFC822EmbeddedNamingStrategyConfigConverter;
import com.github.euler.tika.metadata.DefaultMetadataParserConfigConverter;
import com.github.euler.tika.metadata.MultiMetadataParserConfigConverter;
import com.github.euler.tika.metadata.NameCaseConverterMetadataOperationConfigConverter;
import com.github.euler.tika.metadata.NameReplaceMetadataOperationConfigConverter;
import com.github.euler.tika.metadata.ObjectMetadataParserConfigConverter;
import com.github.euler.tika.metadata.OperationsMetadataParserConfigConverter;
import com.github.euler.tika.metadata.StringMetadataFieldParserConfigConverter;
import com.github.euler.tika.metadata.ValueIntConverterMetadataOperationConfigConverter;
import com.github.euler.tika.metadata.ValueRegexExtractMetadataOperationConfigConverter;
import com.github.euler.tika.metadata.ValueReplaceMetadataOperationConfigConverter;

public class TikaExtension implements EulerExtension {

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return List.of(new TikaContextConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new StripHTMLTaskConfigConverter(),
                new MimeTypeDetectTaskConfigConverter(),
                new ParseTaskConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new DefaultMetadataParserConfigConverter(),
                new ObjectMetadataParserConfigConverter(),
                new MultiMetadataParserConfigConverter(),
                new OperationsMetadataParserConfigConverter(),
                new NameReplaceMetadataOperationConfigConverter(),
                new StringMetadataFieldParserConfigConverter(),
                new NameCaseConverterMetadataOperationConfigConverter(),
                new ValueRegexExtractMetadataOperationConfigConverter(),
                new ValueReplaceMetadataOperationConfigConverter(),
                new ValueIntConverterMetadataOperationConfigConverter(),
                new DefaultEmbeddedNamingStrategyConfigConverter(),
                new MimetypeEmbeddedNamingStrategyConfigConverter(),
                new RFC822EmbeddedNamingStrategyConfigConverter(),
                new DefaultParseContextFactoryConfigConverter(),
                new DefaultEmbeddedStrategyFactoryConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Tika Extension";
    }

}
