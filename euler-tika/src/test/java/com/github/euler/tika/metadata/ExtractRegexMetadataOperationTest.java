package com.github.euler.tika.metadata;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExtractRegexMetadataOperationTest {

    @Test
    public void testExtract() {
        String value = "value1";
        ValueRegexExtractMetadataOperation op = new ValueRegexExtractMetadataOperation("[a-z]+\\d");

        String[] result = (String[]) op.runOnValue("m", value);

        assertEquals(1, result.length);
        assertEquals("value1", result[0]);
    }

    @Test
    public void testExtractMultiple() {
        String value = "value1, value2";
        ValueRegexExtractMetadataOperation op = new ValueRegexExtractMetadataOperation("[a-z]+\\d");

        String[] result = (String[]) op.runOnValue("m", value);

        assertEquals(2, result.length);
        assertEquals("value1", result[0]);
        assertEquals("value2", result[1]);
    }

}
