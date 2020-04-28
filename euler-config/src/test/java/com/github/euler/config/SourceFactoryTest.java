package com.github.euler.config;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.github.euler.core.SourceCommand;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import akka.actor.typed.Behavior;

public class SourceFactoryTest {

    @Test
    public void testLoadSource() {
        Config config = ConfigFactory.parseString("{source = \"test\"}");
        ConfigValue value = config.getValue("source");
        SourceFactory factory = SourceFactory.load();
        Behavior<SourceCommand> sourceBehavior = factory.create(value);
        assertNotNull(sourceBehavior);
    }

    @Test
    public void testLoadSourceWithType() {
        Config config = ConfigFactory.parseString("{source = {type = \"test\"}}");
        ConfigValue value = config.getValue("source");
        SourceFactory factory = SourceFactory.load();
        Behavior<SourceCommand> sourceBehavior = factory.create(value);
        assertNotNull(sourceBehavior);
    }

}
