package com.github.euler.elasticsearch;

import java.io.IOException;

import org.elasticsearch.action.bulk.BulkItemResponse.Failure;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.ScrollableHitSource.SearchFailure;
import org.elasticsearch.join.query.ParentIdQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.CommonContext;
import com.github.euler.core.Item;
import com.github.euler.core.ItemProcessor;
import com.github.euler.core.ProcessingContext;

public class ElasticsearchDeleteParentChildProcessor implements ItemProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchDeleteParentChildProcessor.class);

    private final RestHighLevelClient client;
    private final String childType;
    private final String globalIndex;

    public ElasticsearchDeleteParentChildProcessor(RestHighLevelClient client, String childType, String globalIndex) {
        super();
        this.client = client;
        this.childType = childType;
        this.globalIndex = globalIndex;
    }

    @Override
    public ProcessingContext process(Item item) throws IOException {
        QueryBuilder deleteQuery = buildQuery(item);
        DeleteByQueryRequest req = new DeleteByQueryRequest(getIndex(item.ctx));
        req.setQuery(deleteQuery);
        BulkByScrollResponse resp = client.deleteByQuery(req, RequestOptions.DEFAULT);
        resp.getBulkFailures().forEach(f -> logFailure(f));
        resp.getSearchFailures().forEach(f -> logFailure(f));
        return ProcessingContext.EMPTY;
    }

    private void logFailure(Failure f) {
        LOGGER.error("Delete parent-child failed.", f.getCause());
    }

    private void logFailure(SearchFailure f) {
        LOGGER.error("Delete parent-child failed.", f.getReason());
    }

    private QueryBuilder buildQuery(Item item) {
        String id = (String) item.ctx.context(CommonContext.ID);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.filter(QueryBuilders.boolQuery()
                .should(QueryBuilders.idsQuery().addIds(id))
                .should(new ParentIdQueryBuilder(this.childType, id))
                .minimumShouldMatch(1));
        return query;
    }

    private String getIndex(ProcessingContext ctx) {
        if (ctx.context().containsKey(CommonContext.INDEX)) {
            return (String) ctx.context(CommonContext.INDEX);
        } else {
            return this.globalIndex;
        }
    }

}
