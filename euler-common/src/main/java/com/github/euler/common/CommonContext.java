package com.github.euler.common;

public interface CommonContext {

    final static String TEMPORARY_URI = CommonContext.class.getName() + ".TEMPORARY_URI";
    static final String INDEXABLE = CommonContext.class.getName() + ".INDEXABLE";
    static final String INDEX = CommonContext.class.getName() + ".INDEX";
    static final String PARSED_CONTENT_FILE = CommonContext.class.getName() + ".PARSED_CONTENT_FILE";
    static final String ID = CommonContext.class.getName() + ".ID";
    static final String EXTRACTION_DEPTH = CommonContext.class.getName() + ".EXTRACTION_DEPTH";
    static final String PARSE_ERROR = CommonContext.class.getName() + ".PARSE_ERROR";
}
