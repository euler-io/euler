package com.github.euler.core.source;

public interface SourceNotificationStrategy {

    boolean notificationRequested(int pendingItems, int totalProcessedItems, int totalItems, int totalEmbeddedItems);

}
