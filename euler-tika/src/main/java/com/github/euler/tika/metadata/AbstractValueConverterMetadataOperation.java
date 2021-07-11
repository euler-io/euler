package com.github.euler.tika.metadata;

import java.util.regex.Pattern;

public abstract class AbstractValueConverterMetadataOperation<T> implements MetadataOperation {

    private final Pattern namePattern;
    private final boolean failSafe;

    public AbstractValueConverterMetadataOperation(String nameRegex, boolean failSafe) {
        super();
        this.namePattern = Pattern.compile(nameRegex);
        this.failSafe = failSafe;
    }

    @Override
    public Object runOnValue(String name, Object value) {
        if (namePattern.matcher(name).matches()) {
            try {
                return convert(value.toString());
            } catch (Exception e) {
                if (failSafe) {
                    return getDefaultValueOnError();
                } else {
                    throw new RuntimeException(e);
                }
            }
        } else {
            return value;
        }
    }

    protected abstract T getDefaultValueOnError();

    protected abstract T convert(String value);

}
