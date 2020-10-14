package com.github.euler.elasticsearch;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.github.euler.common.CommonContext;
import com.github.euler.core.AbstractBatchBarrierCondition;
import com.github.euler.core.JobTaskToProcess;

public class ExistsInIndexCondition extends AbstractBatchBarrierCondition {

    private final String index;
    private final RestHighLevelClient client;

    public ExistsInIndexCondition(String index, RestHighLevelClient client) {
        super();
        this.index = index;
        this.client = client;
    }

    @Override
    public List<Boolean> block(List<JobTaskToProcess> msgs) {
        if (!msgs.isEmpty()) {

            String[] ids = msgs.stream()
                    .map(m -> m.ctx.context(CommonContext.ID).toString())
                    .toArray(s -> new String[s]);
            try {
                SearchResponse response = executeQuery(ids);
                Set<String> found = Arrays.asList(response.getHits().getHits()).stream()
                        .map(h -> h.getId()).collect(Collectors.toSet());

                return Arrays.stream(ids)
                        .map(id -> found.contains(id))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return Collections.emptyList();
        }
    }

    private SearchResponse executeQuery(String[] ids) throws IOException {
        SearchRequest req = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(ids.length);
        searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(ids));
        searchSourceBuilder.fetchSource(new String[0], new String[] { "*" });
        req.source(searchSourceBuilder);
        return client.search(req, RequestOptions.DEFAULT);
    }

}
