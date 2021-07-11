package com.github.euler.tika.metadata;

public class ValueIntConverterMetadataOperation extends AbstractValueConverterMetadataOperation<Integer> {

    private final int defaultValueOnError;

    public ValueIntConverterMetadataOperation(String nameRegex, boolean failSafe, int defaultValueOnError) {
        super(nameRegex, failSafe);
        this.defaultValueOnError = defaultValueOnError;
    }

    @Override
    protected Integer getDefaultValueOnError() {
        return defaultValueOnError;
    }

    @Override
    protected Integer convert(String value) {
        return Integer.parseInt(value.toString());
    }

}
