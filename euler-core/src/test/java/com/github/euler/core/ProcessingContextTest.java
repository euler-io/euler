package com.github.euler.core;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ProcessingContextTest {

    @Test
    public void testBuildContextWithMetadata() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key = "key";
        String value = "value";
        builder.metadata(key, value);

        ProcessingContext ctx = builder.build();
        assertEquals(value, ctx.metadata(key));
    }

    @Test
    public void testBuildContextWithMultipleMetadata() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key1 = "key1";
        String value1 = "value1";
        builder.metadata(key1, value1);

        String key2 = "key2";
        String value2 = "value2";
        builder.metadata(key2, value2);

        ProcessingContext ctx = builder.build();
        assertEquals(value1, ctx.metadata(key1));
        assertEquals(value2, ctx.metadata(key2));

    }
}
