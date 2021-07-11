package com.github.euler.tika;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.github.euler.configuration.ConfigContext;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DefaultEmbeddedStrategyConfigConverterTest {

    @Test
    public void testEmptyConvert() {
        Config config = ConfigFactory.parseString("{type: default}");
        ConfigContext configContext = ConfigContext.EMPTY;
        DefaultEmbeddedStrategyFactoryConfigConverter converter = new DefaultEmbeddedStrategyFactoryConfigConverter();
        DefaultEmbeddedStrategy converted = (DefaultEmbeddedStrategy) converter.convert(config, configContext, null).newEmbeddedStrategy(null);
        assertEquals(10, converted.getMaxDepth());
    }

    @Test
    public void testNotEmptyConvert() {
        Config config = ConfigFactory.parseString("{type: default, max-depth: 5}");
        ConfigContext configContext = ConfigContext.EMPTY;
        DefaultEmbeddedStrategyFactoryConfigConverter converter = new DefaultEmbeddedStrategyFactoryConfigConverter();
        DefaultEmbeddedStrategy converted = (DefaultEmbeddedStrategy) converter.convert(config, configContext, null).newEmbeddedStrategy(null);
        assertEquals(5, converted.getMaxDepth());
    }

}
