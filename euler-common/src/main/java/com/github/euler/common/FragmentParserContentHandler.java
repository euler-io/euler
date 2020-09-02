package com.github.euler.common;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class FragmentParserContentHandler extends DefaultHandler {

    private StringBuilder builder;
    private StringBuilder nextFragmentBuilder;
    private FragmentHandler listener;

    private int fragmentSize;
    private int maxLength;

    public FragmentParserContentHandler(FragmentHandler listener) {
        this(1000, 50, listener);
    }

    public FragmentParserContentHandler(int fragmentSize, int fragmentOverlap, FragmentHandler listener) {
        super();
        this.fragmentSize = fragmentSize;
        this.maxLength = fragmentSize + fragmentOverlap;
        this.listener = listener;
    }

    @Override
    public void startDocument() throws SAXException {
        builder = new StringBuilder();
        nextFragmentBuilder = new StringBuilder();
    }

    @Override
    public void characters(char[] chars, int start, int length) throws SAXException {
        // TODO Optimize
        for (char c : chars) {
            builder.append(c);
            if (builder.length() > this.fragmentSize) {
                nextFragmentBuilder.append(c);
            }
            if (builder.length() >= this.maxLength) {
                newFragmentFound();
                builder = nextFragmentBuilder;
                nextFragmentBuilder = new StringBuilder();
            }
        }

    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        if (builder.length() > 0) {
            newFragmentFound();
        }
    }

    private void newFragmentFound() {
        // TODO Use regex
        listener.handleFragment(builder.toString().replaceAll("\u0000", " "));
    }

}