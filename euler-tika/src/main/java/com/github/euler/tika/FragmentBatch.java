package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.github.euler.common.Batch;
import com.github.euler.common.BatchListener;
import com.github.euler.common.FragmentHandler;
import com.github.euler.common.FragmentParserContentHandler;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.FlushTask;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;

public class FragmentBatch implements Batch {

    private static final Logger LOGGER = LoggerFactory.getLogger(FragmentBatch.class);

    private final AutoDetectParser parser;
    private final StreamFactory sf;
    private final int fragmentSize;
    private final int fragmentOverlap;
    private final BatchSink sink;

    private FragmentBatchState state;

    public FragmentBatch(AutoDetectParser parser, StreamFactory sf, int fragmentSize, int fragmentOverlap, BatchSink sink) {
        super();
        this.parser = parser;
        this.sf = sf;
        this.fragmentSize = fragmentSize;
        this.fragmentOverlap = fragmentOverlap;
        this.sink = sink;
    }

    @Override
    public void process(JobTaskToProcess msg, BatchListener listener) {
        String id = sink.store(msg.itemURI, msg.ctx);
        state.itemStored(id, msg);

        if (sf.isEmpty(msg.itemURI)) {
            state.itemParsed(id);
        } else {
            BatchFragmentListener fragmentListener = new BatchFragmentListener(id, listener);
            try {
                parse(msg.itemURI, fragmentListener);
            } catch (IOException | SAXException | TikaException e) {
                throw new RuntimeException(e);
            } finally {
                state.itemParsed(id);
            }
        }
        List<SinkReponse> response = sink.flush(false);
        handleResponse(response, listener);
    }

    @Override
    public void flush(FlushTask msg, BatchListener listener) {
        List<SinkReponse> response = sink.flush(msg.force);
        handleResponse(response, listener);
    }

    public void handleResponse(List<SinkReponse> responses, BatchListener listener) {
        for (SinkReponse response : responses) {
            String id = response.getId();
            if (response.isFailed()) {
                Exception e = response.getFailureCause();
                LOGGER.warn("Sink operation failed.", e);
            }
            boolean finished = state.itemIndexex(id);
            if (finished) {
                JobTaskToProcess msg = state.getMessage(id);
                listener.finished(msg.itemURI, ProcessingContext.EMPTY);
                state.finish(id);
            }
        }
    }

    protected void parse(URI uri, FragmentHandler fragmentHandler) throws IOException, SAXException, TikaException {
        ContentHandler handler = new BodyContentHandler(new FragmentParserContentHandler(fragmentSize, fragmentOverlap, fragmentHandler));
        try (InputStream in = sf.openInputStream(uri)) {
            parser.parse(in, handler, new Metadata());
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
            String fragId = UUID.randomUUID().toString();
            List<SinkReponse> response = sink.storeFragment(id, fragId, count++, frag);
            state.fragmentStored(id, fragId);
            handleResponse(response, listener);
        }

    }

}