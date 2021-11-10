package com.github.euler.barcode;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class BarcodeExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new DefaultBarcodeResultSerializerConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new BarcodeTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Barcode detection extension";
    }

}
