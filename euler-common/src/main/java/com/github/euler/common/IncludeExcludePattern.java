package com.github.euler.common;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class IncludeExcludePattern {

    private final List<Pattern> include;
    private final List<Pattern> exclude;

    public IncludeExcludePattern(List<String> include, List<String> exclude) {
        super();
        this.include = toPattern(include);
        this.exclude = toPattern(exclude);
    }

    public IncludeExcludePattern(String include, String exclude) {
        this(List.of(include), List.of(exclude));
    }

    private List<Pattern> toPattern(List<String> regex) {
        return Collections.unmodifiableList(regex.stream()
                .map(r -> Pattern.compile(r))
                .collect(Collectors.toList()));
    }

    public boolean isIncluded(String value) {
        Objects.requireNonNull(value, "value must not be null");
        boolean included = anyMatches(include, value);
        boolean excluded = anyMatches(exclude, value);

        return included && !excluded;
    }

    private boolean anyMatches(List<Pattern> patterns, String value) {
        return patterns.stream()
                .anyMatch(p -> p.matcher(value).matches());
    }

    public static IncludeExcludePattern fromConfig(Config config) {
        return fromConfig(config, "include-regex", "exclude-regex");
    }

    public static IncludeExcludePattern fromConfig(Config config, String includeField, String excludeField) {
        List<String> include = getStringOrList(config, includeField);
        List<String> exclude = getStringOrList(config, excludeField);
        return new IncludeExcludePattern(include, exclude);
    }

    public static List<String> getStringOrList(Config config, String path) {
        try {
            return List.of(config.getString(path));
        } catch (ConfigException.WrongType e) {
            return config.getStringList(path);
        }
    }

}
