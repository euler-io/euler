package com.github.euler.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class ProcessingContextTest {

    @Test
    public void testBuildProcessingContextWithMetadata() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key = "key";
        String value = "value";
        builder.metadata(key, value);

        ProcessingContext ctx = builder.build();
        assertEquals(value, ctx.metadata(key));
    }

    @Test
    public void testBuildProcessingContextWithMultipleMetadata() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key1 = "key1";
        String value1 = "value1";
        builder.metadata(key1, value1);

        String key2 = "key2";
        String value2 = "value2";
        builder.metadata(key2, value2);

        ProcessingContext ctx = builder.build();
        assertEquals(value1, ctx.metadata(key1));
        assertEquals(value1, ctx.metadata().get(key1));
        assertEquals(2, ctx.metadata().size());

        assertEquals(value2, ctx.metadata(key2));
    }

    @Test
    public void testBuildProcessingContextWithContext() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key = "key";
        String value = "value";
        builder.context(key, value);

        ProcessingContext ctx = builder.build();
        assertEquals(value, ctx.context(key));

    }

    @Test
    public void testBuildProcessingContextWithMultiplesContext() {
        ProcessingContext.Builder builder = ProcessingContext.builder();

        String key1 = "key1";
        String value1 = "value1";
        builder.context(key1, value1);

        String key2 = "key2";
        String value2 = "value2";
        builder.context(key2, value2);

        ProcessingContext ctx = builder.build();
        assertEquals(value1, ctx.context(key1));
        assertEquals(value1, ctx.context().get(key1));
        assertEquals(2, ctx.context().size());

        assertEquals(value2, ctx.context(key2));

    }

    @Test
    public void testMergePutIfAbsentMetadata() {
        ProcessingContext.Builder builder1 = ProcessingContext.builder();
        String key1 = "key1";
        String value1 = "value1";
        builder1.metadata(key1, value1);
        builder1.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx1 = builder1.build();

        ProcessingContext.Builder builder2 = ProcessingContext.builder();
        String key2 = "key2";
        String value2 = "value2";
        builder2.metadata(key2, value2);
        builder2.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx2 = builder2.build();

        ProcessingContext ctx = ctx1.merge(ctx2);

        assertEquals(value1, ctx.metadata(key1));
        assertEquals(value2, ctx.metadata(key2));
    }

    @Test
    public void testMergePutIfAbsentMetadataWontReplace() {
        ProcessingContext.Builder builder1 = ProcessingContext.builder();
        String key1 = "key1";
        String value1 = "value1";
        builder1.metadata(key1, value1);
        builder1.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx1 = builder1.build();

        ProcessingContext.Builder builder2 = ProcessingContext.builder();
        String value2 = "value2";
        builder2.metadata(key1, value2);
        builder2.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx2 = builder2.build();

        ProcessingContext ctx = ctx1.merge(ctx2);

        assertEquals(value1, ctx.metadata(key1));
    }

    @Test
    public void testMergeOverwriteMetadata() {
        ProcessingContext.Builder builder1 = ProcessingContext.builder();
        String key1 = "key1";
        String value1 = "value1";
        builder1.metadata(key1, value1);
        builder1.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx1 = builder1.build();

        ProcessingContext.Builder builder2 = ProcessingContext.builder();
        String value2 = "value2";
        builder2.metadata(key1, value2);
        builder2.setAction(ProcessingContext.Action.OVERWRITE);

        ProcessingContext ctx2 = builder2.build();

        ProcessingContext ctx = ctx1.merge(ctx2);

        assertEquals(value2, ctx.metadata(key1));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMergeListMetadata() {
        ProcessingContext.Builder builder1 = ProcessingContext.builder();
        String key1 = "key1";
        String value1 = "value1";
        builder1.metadata(key1, value1);

        String collectionKey = "col1";
        builder1.metadata(collectionKey, Arrays.asList("a"));
        builder1.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx1 = builder1.build();

        ProcessingContext.Builder builder2 = ProcessingContext.builder();
        String key2 = "key2";
        String value2 = "value2";
        builder2.metadata(key2, value2);
        builder2.metadata(collectionKey, Arrays.asList("b"));
        builder2.setAction(ProcessingContext.Action.MERGE);

        ProcessingContext ctx2 = builder2.build();

        ProcessingContext ctx = ctx1.merge(ctx2);

        assertEquals(value1, ctx.metadata(key1));
        assertEquals(value2, ctx.metadata(key2));
        assertEquals(3, ctx.metadata().size());

        List<String> list = (List<String>) ctx.metadata(collectionKey);
        assertEquals(2, list.size());
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMergeSetMetadata() {
        ProcessingContext.Builder builder1 = ProcessingContext.builder();
        String key1 = "key1";
        String value1 = "value1";
        builder1.metadata(key1, value1);

        String collectionKey = "col1";
        builder1.metadata(collectionKey, new HashSet(Arrays.asList("a")));
        builder1.setAction(ProcessingContext.Action.PUT_IF_ABSENT);

        ProcessingContext ctx1 = builder1.build();

        ProcessingContext.Builder builder2 = ProcessingContext.builder();
        String key2 = "key2";
        String value2 = "value2";
        builder2.metadata(key2, value2);
        builder2.metadata(collectionKey, new HashSet(Arrays.asList("b")));
        builder2.setAction(ProcessingContext.Action.MERGE);

        ProcessingContext ctx2 = builder2.build();

        ProcessingContext ctx = ctx1.merge(ctx2);

        assertEquals(value1, ctx.metadata(key1));
        assertEquals(value2, ctx.metadata(key2));
        assertEquals(3, ctx.metadata().size());

        Set<String> set = (Set<String>) ctx.metadata(collectionKey);
        assertEquals(2, set.size());
        assertTrue(set.contains("a"));
        assertTrue(set.contains("b"));
    }
}
