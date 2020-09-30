package com.github.euler.common;

import java.net.URL;
import java.util.Locale;

import com.github.euler.configuration.AbstractBarrierConditionConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.BarrierCondition;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DateOrSizeModificationConditionConfigConverter extends AbstractBarrierConditionConfigConverter {

    @Override
    public String configType() {
        return "date-or-size-modification";
    }

    @Override
    public BarrierCondition convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        String dateFormat = config.getString("date-format");
        Locale dateLocale = new Locale(config.getString("date-locale.language"), config.getString("date-locale.country"));
        return new DateOrSizeModificationCondition(dateFormat, dateLocale);
    }

    protected Config getDefaultConfig() {
        URL resource = DateOrSizeModificationConditionConfigConverter.class.getClassLoader().getResource("dateorsizemodificationcondition.conf");
        return ConfigFactory.parseURL(resource);
    }

}
