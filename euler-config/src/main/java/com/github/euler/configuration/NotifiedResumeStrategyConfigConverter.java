package com.github.euler.configuration;

import com.github.euler.core.resume.NotifiedResumeStrategy;
import com.github.euler.core.resume.ResumeStrategy;
import com.typesafe.config.Config;

public class NotifiedResumeStrategyConfigConverter extends AbstractResumeStrategyConfigConverter {

    @Override
    public String configType() {
        return "notified";
    }

    @Override
    public ResumeStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new NotifiedResumeStrategy();
    }

}
