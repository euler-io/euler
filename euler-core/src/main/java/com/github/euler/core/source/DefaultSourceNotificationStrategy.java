package com.github.euler.core.source;

import java.time.Duration;
import java.time.Instant;

public class DefaultSourceNotificationStrategy implements SourceNotificationStrategy {

    private final int minProcessedItems;
    private final int maxProcessedItems;
    private final Duration minInterval;
    private final Duration maxInterval;
    private final int maxNumberIgnoredRequests;

    private Instant lastNotification = null;
    private int lastTotalProcessedItems = 0;
    private int numOfIgnoredRequests = 0;

    public DefaultSourceNotificationStrategy(int minProcessedItems, int maxProcessedItems, Duration minInterval, Duration maxInterval, int maxNumberIgnoredRequests) {
        super();
        this.minProcessedItems = minProcessedItems;
        this.maxProcessedItems = maxProcessedItems;
        this.minInterval = minInterval;
        this.maxInterval = maxInterval;
        this.maxNumberIgnoredRequests = maxNumberIgnoredRequests;
    }

    public DefaultSourceNotificationStrategy() {
        this(0, Integer.MAX_VALUE, Duration.ZERO, Duration.ofSeconds(2), 30);
    }

    @Override
    public boolean notificationRequested(int pendingItems, int totalProcessedItems, int totalItems, int totalEmbeddedItems) {
        boolean numProcessedItemsExceeded = isNumProcessedItemsExceeded(totalProcessedItems);
        boolean intervalExceeded = isIntervalExceeded();
        boolean numRequestsExceeded = isNumRequestsExceeded();
        boolean numPendingItemsReachedZero = pendingItems == 0;

        this.lastTotalProcessedItems = totalProcessedItems;
        this.lastNotification = Instant.now();

        if (numProcessedItemsExceeded || intervalExceeded || numRequestsExceeded || numPendingItemsReachedZero) {
            return true;
        } else {
            this.numOfIgnoredRequests++;
            return false;
        }
    }

    private boolean isNumRequestsExceeded() {
        return maxNumberIgnoredRequests >= 0 && numOfIgnoredRequests >= maxNumberIgnoredRequests;
    }

    private boolean isIntervalExceeded() {
        Duration durationSinceLastNotification = getDurationSinceLastNotification();
        return durationSinceLastNotification.compareTo(minInterval) > 0 && durationSinceLastNotification.compareTo(maxInterval) >= 0;
    }

    private Duration getDurationSinceLastNotification() {
        if (this.lastNotification == null) {
            return Duration.ZERO;
        } else {
            return Duration.between(this.lastNotification, Instant.now());
        }
    }

    private boolean isNumProcessedItemsExceeded(int totalProcessedItems) {
        int processed = totalProcessedItems - this.lastTotalProcessedItems;
        return processed > minProcessedItems && processed >= maxProcessedItems;
    }

}
