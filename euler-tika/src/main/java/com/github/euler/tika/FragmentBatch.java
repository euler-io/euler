package com.github.euler.tika;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import com.github.euler.common.Batch;
import com.github.euler.common.BatchListener;
import com.github.euler.common.FragmentHandler;
import com.github.euler.common.FragmentParserContentHandler;
import com.github.euler.common.StreamFactory;
import com.github.euler.core.FlushTask;
import com.github.euler.core.JobTaskToProcess;

public class FragmentBatch implements Batch {

    private final AutoDetectParser parser;
    private final StreamFactory sf;
    private final int fragmentSize;
    private final int fragmentOverlap;
    private final BatchSink sink;

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
        BatchFragmentListener fragmentListener = new BatchFragmentListener(id, listener);
        try {
            parse(msg.itemURI, fragmentListener);
        } catch (IOException | SAXException | TikaException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void flush(FlushTask msg, BatchListener listener) {
        SinkReponse response = sink.flush(msg.force);
        handleResponse(response, listener);
    }

    public void handleResponse(SinkReponse response, BatchListener listener) {
        
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
        final BatchListener listener;

        public BatchFragmentListener(String id, BatchListener listener) {
            super();
            this.id = id;
            this.listener = listener;
        }

        @Override
        public void handleFragment(String frag) {
            SinkReponse response = sink.storeFragment(id, frag);
            handleResponse(response, listener);
        }

    }

}
