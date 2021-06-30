package com.github.euler.common;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.Test;
import org.xml.sax.ContentHandler;

public class FragmentParserContentHandlerTest {

    @Test
    public void testFragmentSmallerThanSize() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String item = "12345";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(item.toCharArray(), 0, item.length());
        handler.endDocument();

        assertEquals(1, fragments.size());
        assertEquals(item, fragments.get(0));
    }

    @Test
    public void testFragmentSize() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String item = "123456789012";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(item.toCharArray(), 0, item.length());
        handler.endDocument();

        assertEquals(2, fragments.size());
        assertEquals(item, fragments.get(0));
        assertEquals("12", fragments.get(1));
    }

    @Test
    public void testFragmentsSmallerThanSize() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "0123";
        String frag2 = "4";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.characters(frag2.toCharArray(), 0, frag2.length());
        handler.endDocument();

        assertEquals(1, fragments.size());
        assertEquals("01234", fragments.get(0));
    }

    @Test
    public void testFragmentExactSize() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "1234567890";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.endDocument();

        assertEquals(1, fragments.size());
        assertEquals("1234567890", fragments.get(0));
    }

    @Test
    public void testFragmentsExactSize() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "12345";
        String frag2 = "67890";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.characters(frag2.toCharArray(), 0, frag1.length());
        handler.endDocument();

        assertEquals(1, fragments.size());
        assertEquals("1234567890", fragments.get(0));
    }

    @Test
    public void testFragmentExactSizeAndExactOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "123456789012";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.endDocument();

        assertEquals(2, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("12", fragments.get(1));
    }

    @Test
    public void testFragmentsExactSizeAndExactOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "1234567";
        String frag2 = "89012";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.characters(frag2.toCharArray(), 0, frag2.length());
        handler.endDocument();

        assertEquals(2, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("12", fragments.get(1));
    }

    @Test
    public void testFragmentBiggerThanSizeAndOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "1234567890123";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.endDocument();

        assertEquals(2, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("123", fragments.get(1));
    }

    @Test
    public void testFragmentsBiggerThanSizeAndOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "1234";
        String frag2 = "56789";
        String frag3 = "0123";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.characters(frag2.toCharArray(), 0, frag2.length());
        handler.characters(frag3.toCharArray(), 0, frag3.length());
        handler.endDocument();

        assertEquals(2, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("123", fragments.get(1));
    }

    @Test
    public void testFragmentTwiceBiggerThanSizeAndOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "12345678901234567890123";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.endDocument();

        assertEquals(3, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("123456789012", fragments.get(1));
        assertEquals("123", fragments.get(2));
    }

    @Test
    public void testFragmentsTwiceBiggerThanSizeAndOverlap() throws Exception {
        int fragmentSize = 10;
        int fragmentOverlap = 2;

        String frag1 = "123";
        String frag2 = "45678";
        String frag3 = "9012345";
        String frag4 = "6789";
        String frag5 = "0123";
        List<String> fragments = new ArrayList<>();

        FragmentHandler listener = (String frag) -> fragments.add(frag);
        FragmentParserContentHandler handler = new FragmentParserContentHandler(fragmentSize, fragmentOverlap, listener);

        handler.startDocument();
        handler.characters(frag1.toCharArray(), 0, frag1.length());
        handler.characters(frag2.toCharArray(), 0, frag2.length());
        handler.characters(frag3.toCharArray(), 0, frag3.length());
        handler.characters(frag4.toCharArray(), 0, frag4.length());
        handler.characters(frag5.toCharArray(), 0, frag5.length());
        handler.endDocument();

        assertEquals(3, fragments.size());
        assertEquals("123456789012", fragments.get(0));
        assertEquals("123456789012", fragments.get(1));
        assertEquals("123", fragments.get(2));
    }

    @Test
    public void testParseFile() throws Exception {
        Parser parser = new AutoDetectParser();

        List<String> fragments = new ArrayList<>();
        FragmentHandler listener = (String frag) -> fragments.add(frag);

        ContentHandler handler = new BodyContentHandler(new FragmentParserContentHandler(1000, 50, listener));
        try (InputStream in = FragmentParserContentHandlerTest.class.getClassLoader().getResourceAsStream("sample-file.txt")) {
            parser.parse(in, handler, new Metadata(), new ParseContext());
        }

        assertEquals(1, fragments.size());
        assertEquals("File Content\n", fragments.get(0));
    }

}
