package com.github.euler.tika;

public interface SinkItemResponse {

    String getId();

    boolean isFailed();

    Exception getFailureCause();

}
