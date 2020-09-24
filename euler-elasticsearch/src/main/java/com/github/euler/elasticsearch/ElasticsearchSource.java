package com.github.euler.elasticsearch;

import java.io.IOException;
import java.util.Collections;
import java.util.function.Supplier;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentLocation;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.AbstractQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.core.AbstractPausableSource;
import com.github.euler.core.SourceListener;

public class ElasticsearchSource extends AbstractPausableSource implements DeprecationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSource.class);

    private final SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());

    private final RestHighLevelClient client;
    private final String index;
    private final String query;
    private final int size;
    private final String scroll;

    private SearchResponse response;

    protected ElasticsearchSource(RestHighLevelClient client, String index, String query, int size, String scroll, SearchResponse response) {
        super();
        this.client = client;
        this.index = index;
        this.query = query;
        this.size = size;
        this.scroll = scroll;
        this.response = response;
    }

    private QueryBuilder parseQuery(String jsonQuery) {
        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(new NamedXContentRegistry(searchModule
                .getNamedXContents()), this, jsonQuery)) {
            return AbstractQueryBuilder.parseInnerQueryBuilder(parser);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean doScan(SourceListener listener) throws IOException {
        if (this.response == null) {
            this.response = executeQuery(query);
        } else {
            this.response = doScroll(this.response);
        }

        return false;
    }

    private SearchResponse doScroll(SearchResponse lastResponse) {

        return null;
    }

    private SearchResponse executeQuery(String jsonQuery) {
        SearchRequest req = new SearchRequest(this.index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(this.size);
        searchSourceBuilder.query(this.parseQuery(this.query));
        return null;
    }

    @Override
    public void usedDeprecatedName(String parserName, Supplier<XContentLocation> location, String usedName, String modernName) {
        LOGGER.warn("{} is deprecated. Use {} instead.", usedName, modernName);
    }

    @Override
    public void usedDeprecatedField(String parserName, Supplier<XContentLocation> location, String usedName, String replacedWith) {
        LOGGER.warn("{} is deprecated. Use {} instead.", usedName, replacedWith);
    }

    @Override
    public void usedDeprecatedField(String parserName, Supplier<XContentLocation> location, String usedName) {
        LOGGER.warn("{} is deprecated.", usedName);
    }

}
