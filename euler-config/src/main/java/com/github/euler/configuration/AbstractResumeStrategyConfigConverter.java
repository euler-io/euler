package com.github.euler.configuration;

import com.github.euler.core.resume.ResumeStrategy;

public abstract class AbstractResumeStrategyConfigConverter implements TypeConfigConverter<ResumeStrategy> {

    public static final String TYPE = "resume-strategy";

    @Override
    public String type() {
        return TYPE;
    }

}
