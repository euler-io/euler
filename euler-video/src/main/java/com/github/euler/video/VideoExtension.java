package com.github.euler.video;

import java.util.List;

import com.github.euler.configuration.EulerExtension;
import com.github.euler.configuration.TaskConfigConverter;
import com.github.euler.configuration.TypeConfigConverter;

public class VideoExtension implements EulerExtension {

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return List.of(
                new FfmpegVideoPreviewTaskConfigConverter(),
                new FastFFmpegVideoThumbnailTaskConfigConverter(),
                new FfmpegVideoEncodeTaskConfigConverter());
    }

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return List.of(
                new UrlInputFactoryConfigConverter(),
                new UrlOutputFactoryConfigConverter(),
                new CustomArgsInputFactoryConfigConverter(),
                new CustomArgsOutputFactoryConfigConverter());
    }

}
