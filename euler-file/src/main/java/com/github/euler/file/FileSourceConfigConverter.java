package com.github.euler.file;

import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.github.euler.configuration.AbstractSourceConfigConverter;
import com.github.euler.configuration.ConfigContext;
import com.github.euler.configuration.TypesConfigConverter;
import com.github.euler.core.PausableSourceExecution;
import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import akka.actor.typed.Behavior;

public class FileSourceConfigConverter extends AbstractSourceConfigConverter {

    @Override
    public String configType() {
        return "file";
    }

    @Override
    public Behavior<SourceCommand> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
        config = config.withFallback(getDefaultConfig());
        FileSource.Builder builder = FileSource.builder();
        builder.setMaxItemsPerYield(config.getInt("max-items-per-yield"));
        builder.setNotifyDirectories(config.getBoolean("notify-directories"));

        int flags = getFlags(config.getStringList("regex-flags"));
        Pattern regex = Pattern.compile(config.getString("regex"), flags);
        builder.setRegex(regex);

        return PausableSourceExecution.create(builder.build());
    }

    protected Integer getFlags(List<String> flags) {
        return flags.stream()
                .map(f -> {
                    try {
                        return Pattern.class.getField(f).getInt(null);
                    } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.reducing(0, (i1, i2) -> i1 | i2));
    }

    protected Config getDefaultConfig() {
        URL resource = FileSourceConfigConverter.class.getClassLoader().getResource("filesource.conf");
        return ConfigFactory.parseURL(resource);
    }

}
