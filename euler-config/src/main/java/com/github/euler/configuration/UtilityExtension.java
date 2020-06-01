package com.github.euler.configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import com.github.euler.core.SourceCommand;
import com.github.euler.core.Sources;
import com.github.euler.core.Task;
import com.github.euler.core.Tasks;
import com.typesafe.config.Config;

import akka.actor.typed.Behavior;

public class UtilityExtension implements EulerExtension {

    @Override
    public List<TypeConfigConverter<?>> typeConverters() {
        return Arrays.asList(
                new AbstractSourceConfigConverter() {

                    @Override
                    public String configType() {
                        return "fixed";
                    }

                    @Override
                    public Behavior<SourceCommand> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
                        try {
                            URI uri = new URI(config.getString("uri"));
                            return Sources.fixedItemBehavior(uri);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    }

                },
                new AbstractSourceConfigConverter() {

                    @Override
                    public String configType() {
                        return "empty";
                    }

                    @Override
                    public Behavior<SourceCommand> convert(Config config, ConfigContext configContext, TypesConfigConverter typeConfigConverter) {
                        return Sources.emptyBehavior();
                    }

                });
    }

    @Override
    public List<TaskConfigConverter> taskConverters() {
        return Arrays.asList(
                new AbstractTaskConfigConverter() {

                    @Override
                    public String type() {
                        return "empty";
                    }

                    @Override
                    public Task convert(Config config, ConfigContext ctx, TypesConfigConverter typeConfigConverter, TasksConfigConverter tasksConfigConverter) {
                        String name = getName(config, tasksConfigConverter);
                        return Tasks.empty(name);
                    }

                });
    }

}
