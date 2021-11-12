package com.github.euler.stt;

import com.github.euler.configuration.TypeConfigConverter;

public abstract class AbstractVoskRecognizerLoaderConfigConverter implements TypeConfigConverter<VoskRecognizerLoader> {

    public static final String TYPE = "vosk-recognizer-loader";

    @Override
    public String type() {
        return TYPE;
    }

}
