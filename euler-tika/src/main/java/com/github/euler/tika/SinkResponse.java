package com.github.euler.tika;

import java.util.List;

public interface SinkResponse {

    String getId();

    List<SinkItemResponse> getResponses();

}
