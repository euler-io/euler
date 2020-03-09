package com.github.euler.tika;

public interface SinkReponse {

    String getId();

    boolean isFailed();

    Exception getFailureCause();

}
