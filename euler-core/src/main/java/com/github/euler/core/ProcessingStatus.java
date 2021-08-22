package com.github.euler.core;

public class ProcessingStatus implements SourceCommand {

    public final int pendingItems;
    public final int totalProcessedItems;
    public final int totalItems;
    public final int totalEmbeddedItems;

    public ProcessingStatus(int pendingItems, int totalProcessedItems, int totalItems, int totalEmbeddedItems) {
        this.pendingItems = pendingItems;
        this.totalProcessedItems = totalProcessedItems;
        this.totalItems = totalItems;
        this.totalEmbeddedItems = totalEmbeddedItems;
    }

    @Override
    public String toString() {
        return "ProcessingStatus [pendingItems=" + pendingItems + ", totalProcessedItems=" + totalProcessedItems + ", totalItems=" + totalItems + ", totalEmbeddedItems="
                + totalEmbeddedItems + "]";
    }

}
