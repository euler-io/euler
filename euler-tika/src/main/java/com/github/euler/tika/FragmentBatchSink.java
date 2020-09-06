package com.github.euler.tika;

public interface FragmentBatchSink extends MetadataBatchSink {

    SinkResponse storeFragment(String parentId, int index, String fragment);

}
