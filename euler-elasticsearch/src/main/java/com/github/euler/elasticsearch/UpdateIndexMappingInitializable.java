package com.github.euler.elasticsearch;

import java.io.IOException;
import java.util.List;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.xcontent.XContentType;

import com.github.euler.configuration.ConfigUtil;
import com.github.euler.core.Initializable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;

public class UpdateIndexMappingInitializable implements Initializable {

    private final RestHighLevelClient client;
    private final Config indexConfig;

    public UpdateIndexMappingInitializable(RestHighLevelClient client, Config indexConfig) {
        super();
        this.client = client;
        this.indexConfig = indexConfig;
    }

    @Override
    public void initialize() throws IOException {
        List<String> indices = ConfigUtil.getStringOrList(indexConfig, "index");
        Config properties = ConfigFactory.empty().withValue("properties", indexConfig.getValue("properties"));
        PutMappingRequest req = new PutMappingRequest(indices.stream().toArray(s -> new String[s]));
        String jsonProperties = properties.root().render(ConfigRenderOptions.concise());
        req.source(jsonProperties, XContentType.JSON);

        client.indices().putMapping(req, RequestOptions.DEFAULT);
    }

}
