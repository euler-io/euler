package com.github.euler.tika;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.euler.common.Batch;
import com.github.euler.common.BatchListener;
import com.github.euler.core.Flush;
import com.github.euler.core.JobTaskToProcess;
import com.github.euler.core.ProcessingContext;

public class MetadataBatch implements Batch {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetadataBatch.class);

    private final MetadataBatchSink sink;

    private MetadataBatchState state;

    public MetadataBatch(MetadataBatchSink sink) {
        super();
        this.sink = sink;
        this.state = new MetadataBatchState();
    }

    @Override
    public void process(JobTaskToProcess msg, BatchListener listener) {
        SinkResponse response = sink.store(msg.itemURI, msg.ctx);
        state.itemProcessed(response.getId(), msg);
        handleResponse(response, listener);
    }

    protected void handleResponse(SinkResponse response, BatchListener listener) {
        for (SinkItemResponse itemResponse : response.getResponses()) {
            if (itemResponse.isFailed()) {
                Exception e = itemResponse.getFailureCause();
                LOGGER.warn("Sink operation failed.", e);
            }
            String id = itemResponse.getId();
            JobTaskToProcess msg = state.getMessage(id);
            listener.finished(msg.itemURI, ProcessingContext.EMPTY);
            state.finish(id);
        }
    }

    @Override
    public void flush(Flush msg, BatchListener listener) {
        SinkResponse response = sink.flush(msg.force);
        handleResponse(response, listener);
    }

    @Override
    public void finish() {
        sink.finish();
    }

}
