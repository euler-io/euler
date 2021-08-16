package com.github.euler.elasticsearch;

import java.io.IOException;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;

import com.github.euler.core.Initializable;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigRenderOptions;

public class CreateIndexInitializable implements Initializable {

    private final RestHighLevelClient client;
    private final Config indexConfig;

    public CreateIndexInitializable(RestHighLevelClient client, Config indexConfig) {
        super();
        this.client = client;
        this.indexConfig = indexConfig;
    }

    @Override
    public void initialize() throws IOException {
        String indexName = indexConfig.getString("name");

        if (!exists(indexName)) {
            CreateIndexRequest req = new CreateIndexRequest(indexName);

            String jsonMappings = indexConfig.getConfig("mappings").root().render(ConfigRenderOptions.concise());
            req.mapping(jsonMappings, XContentType.JSON);
            String jsonSetttings = indexConfig.getConfig("settings").root().render(ConfigRenderOptions.concise());
            req.settings(jsonSetttings, XContentType.JSON);

            client.indices().create(req, RequestOptions.DEFAULT);
        }
    }

    private boolean exists(String indexName) throws IOException {
        GetIndexRequest req = new GetIndexRequest(indexName);
        return client.indices().exists(req, RequestOptions.DEFAULT);
    }

}
