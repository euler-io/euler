package com.github.euler.stt;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class VoskExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new FSVoskRecognizerLoaderConfigConverter());
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new VoskSttTaskConfigConverter());
    }

    @Override
    public String getDescription() {
        return "Speech Recognition Module using VOSK";
    }

}
