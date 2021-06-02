package com.github.euler.tika.metadata;

import java.util.List;

import org.apache.tika.metadata.Metadata;

import com.github.euler.core.ProcessingContext;
import com.github.euler.core.ProcessingContext.Builder;

public class OperationsMetadataParser implements MetadataParser {

    private final MetadataParser wrapped;
    private List<MetadataOperation> operations;

    public OperationsMetadataParser(MetadataParser wrapped, List<MetadataOperation> operations) {
        super();
        this.wrapped = wrapped;
        this.operations = operations;
    }

    @Override
    public ProcessingContext parse(Metadata metadata) {
        ProcessingContext ctx = wrapped.parse(metadata);
        Builder builder = ProcessingContext.builder();
        ctx.metadata().forEach((n, v) -> {
            for (MetadataOperation op : operations) {
                n = op.runOnName(n);
                v = op.runOnValue(n, v);
            }
            builder.metadata(n, v);
        });
        return builder.build();
    }

}
