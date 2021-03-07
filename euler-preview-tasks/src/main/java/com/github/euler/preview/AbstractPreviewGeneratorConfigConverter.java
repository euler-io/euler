package com.github.euler.preview;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractPreviewGeneratorConfigConverter implements TypeConfigConverter<PreviewGenerator> {

    public static final String PREVIEW_GENERATOR = "preview-generator";

    @Override
    public String type() {
        return PREVIEW_GENERATOR;
    }
    
    

}
