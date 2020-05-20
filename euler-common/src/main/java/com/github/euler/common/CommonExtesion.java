package com.github.euler.common;

import java.util.Arrays;
import java.util.List;

import com.github.euler.configuration.ContextConfigConverter;
import com.github.euler.configuration.EulerExtension;

public class CommonExtesion implements EulerExtension {

    @Override
    public List<ContextConfigConverter> pathConverters() {
        return Arrays.asList(new StreamFactoryContextConfigConverter());
    }

}
