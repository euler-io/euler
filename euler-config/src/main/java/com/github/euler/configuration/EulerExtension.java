package com.github.euler.configuration;

import java.util.Collections;
import java.util.List;

public interface EulerExtension {

    default List<ContextConfigConverter> pathConverters() {
        return Collections.emptyList();
    };

    default List<TypeConfigConverter<?>> typeConverters() {
        return Collections.emptyList();
    };

    default List<TaskConfigConverter> taskConverters() {
        return Collections.emptyList();
    };

    default String getDescription() {
        return "";
    }

}
