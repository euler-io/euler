package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.github.euler.common.Batch;
import com.github.euler.common.BatchListener;
import com.github.euler.common.CommonContext;
import com.github.euler.common.CommonMetadata;
import com.github.euler.common.FragmentHandler;
import com.github.euler.common.FragmentParserContentHandler;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.Flush;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;

public class FragmentBatch implements Batch {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private final Parser parser;
    private final StreamFactory sf;
    private final int fragmentSize;
    private final int fragmentOverlap;
    private final FragmentBatchSink sink;

    private FragmentBatchState state;

    public FragmentBatch(Parser parser, StreamFactory sf, int fragmentSize, int fragmentOverlap, FragmentBatchSink sink) {
        super();
        this.parser = parser;
        this.sf = sf;
        this.fragmentSize = fragmentSize;
        this.fragmentOverlap = fragmentOverlap;
        this.sink = sink;
        this.state = new FragmentBatchState();
    }

    @Override
    public void process(JobTaskToProcess msg, BatchListener listener) {
        SinkResponse response = sink.store(msg.itemURI, msg.ctx);

        String id = response.getId();
        state.itemStored(id, msg);

        boolean parseError = msg.ctx.context(CommonContext.PARSE_ERROR, false);
        URI uri = (URI) msg.ctx.context(CommonContext.PARSED_CONTENT_FILE);
        boolean isDirectory = msg.ctx.metadata(CommonMetadata.IS_DIRECTORY, false);
        if (uri == null || sf.isEmpty(uri, msg.ctx) || isDirectory || parseError) {
            state.itemParsed(id);
        } else {
            BatchFragmentListener fragmentListener = new BatchFragmentListener(id, listener);
            try {
                parse(uri, msg.ctx, fragmentListener);
            } catch (IOException | SAXException | TikaException e) {
                LOGGER.warn("Error parsing {} for {}.", uri, e.getMessage());
            } finally {
                state.itemParsed(id);
            }
        }
        handleResponse(response, listener);
    }

    @Override
    public void flush(Flush msg, BatchListener listener) {
        SinkResponse response = sink.flush(msg.force);
        handleResponse(response, listener);
    }

    public void handleResponse(SinkResponse response, BatchListener listener) {
        for (SinkItemResponse itemResponse : response.getResponses()) {
            if (itemResponse.isFailed()) {
                Exception e = itemResponse.getFailureCause();
                LOGGER.warn("Sink operation failed.", e);
            }
            String id = itemResponse.getId();
            boolean finished = state.itemIndexed(id);
            if (finished) {
                String parentId = state.getParent(id);
                JobTaskToProcess msg = state.getMessage(parentId);
                listener.finished(msg.itemURI, ProcessingContext.EMPTY);
                state.finish(parentId);
            }
        }
    }

    protected void parse(URI uri, ProcessingContext ctx, FragmentHandler fragmentHandler) throws IOException, SAXException, TikaException {
        ContentHandler handler = new BodyContentHandler(new FragmentParserContentHandler(fragmentSize, fragmentOverlap, fragmentHandler));
        String charset = ctx.context(CommonContext.CHARSET, "UTF-8");
        try (InputStream in = sf.openInputStream(uri, ctx)) {
            Metadata metadata = new Metadata();
            metadata.add(Metadata.CONTENT_ENCODING, charset);
            parser.parse(in, handler, metadata, new ParseContext());
        }
    }

    @Override
    public void finish() {
        sink.finish();
    }

    private class BatchFragmentListener implements FragmentHandler {

        final String id;
        int count = 0;
        final BatchListener listener;

        public BatchFragmentListener(String id, BatchListener listener) {
            super();
            this.id = id;
            this.listener = listener;
        }

        @Override
        public void handleFragment(String frag) {
            SinkResponse response = sink.storeFragment(id, count++, frag);
            String fragId = response.getId();
            state.fragmentStored(id, fragId);
            handleResponse(response, listener);
        }

    }

}
