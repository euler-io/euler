package com.github.euler.elasticsearch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.function.Supplier;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RequestOptions;
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
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.core.AbstractPausableSource;
import com.github.euler.core.ProcessingContext;
import com.github.euler.core.SourceListener;

public class ElasticsearchSource extends AbstractPausableSource implements DeprecationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ElasticsearchSource.class);

    private final SearchModule searchModule = new SearchModule(Settings.EMPTY, false, Collections.emptyList());

    private final RestHighLevelClient client;
    private final String query;
    private final int size;
    private final String scrollKeepAlive;

    private URI uri;
    private SearchResponse response;

    protected ElasticsearchSource(RestHighLevelClient client, String query, int size, String scrollKeepAlive) {
        super();
        this.client = client;
        this.query = query;
        this.size = size;
        this.scrollKeepAlive = scrollKeepAlive;
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
    public void prepareScan(URI uri) throws IOException {
        this.uri = uri;
    }

    @Override
    public boolean doScan(SourceListener listener) throws IOException {
        if (this.response == null) {
            this.response = executeQuery(query);
        } else {
            this.response = doScroll(this.response);
        }

        SearchHit[] hits = this.response.getHits().getHits();
        for (SearchHit hit : hits) {
            notify(hit, listener);
        }
        return hits.length == 0;
    }

    private void notify(SearchHit hit, SourceListener listener) {
        try {
            URI itemURI = buildURI(hit);
            ProcessingContext ctx = buildContext(hit);
            listener.notifyItemFound(itemURI, itemURI, ctx);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private ProcessingContext buildContext(SearchHit hit) {
        ProcessingContext.Builder builder = ProcessingContext.builder();
        hit.getSourceAsMap().forEach((k, v) -> builder.context(k, v));
        return builder.build();
    }

    private URI buildURI(SearchHit hit) throws URISyntaxException {
        String host = uri.getHost();
        String hitIndex = hit.getIndex();
        String hitId = hit.getId();
        String uri = String.format("elasticsearch://%s/%s/%s?index=%s&id=%s", host, hitIndex, hitId, hitIndex, hitId);
        return new URI(uri);
    }

    private SearchResponse doScroll(SearchResponse lastResponse) throws IOException {
        SearchScrollRequest req = new SearchScrollRequest(lastResponse.getScrollId());
        req.scroll(this.scrollKeepAlive);
        return client.scroll(req, RequestOptions.DEFAULT);
    }

    private SearchResponse executeQuery(String jsonQuery) throws IOException {
        String index = getIndex();
        SearchRequest req = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.size(this.size);
        searchSourceBuilder.query(this.parseQuery(this.query));
        req.scroll(this.scrollKeepAlive);
        return client.search(req, RequestOptions.DEFAULT);
    }

    private String getIndex() {
        return this.uri.getPath().substring(1);
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
