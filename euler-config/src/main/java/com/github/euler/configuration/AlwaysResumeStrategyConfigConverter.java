package com.github.euler.configuration;

import com.github.euler.core.resume.AlwaysResumeStrategy;
import com.github.euler.core.resume.ResumeStrategy;
import com.typesafe.config.Config;

public class AlwaysResumeStrategyConfigConverter extends AbstractResumeStrategyConfigConverter {

    @Override
    public String configType() {
        return "always";
    }

    @Override
    public ResumeStrategy convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        return new AlwaysResumeStrategy();
    }

}
