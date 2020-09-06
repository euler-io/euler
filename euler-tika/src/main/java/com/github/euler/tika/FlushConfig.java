package com.github.euler.tika;

public class FlushConfig {

    private int minActionsToFlush = 0;
    private long minBytesToFlush = 0;
    private int maxActionsToFlush = 1000;
    private long maxBytesToFlush = 10 * 1000000;

    public FlushConfig() {
        super();
    }

    public FlushConfig(int minActionsToFlush, int maxActionsToFlush, long minBytesToFlush, long maxBytesToFlush) {
        super();
        this.minActionsToFlush = minActionsToFlush;
        this.maxActionsToFlush = maxActionsToFlush;
        this.minBytesToFlush = minBytesToFlush;
        this.maxBytesToFlush = maxBytesToFlush;
    }

    public int getMinActionsToFlush() {
        return minActionsToFlush;
    }

    public void setMinActionsToFlush(int minActionsToFlush) {
        this.minActionsToFlush = minActionsToFlush;
    }

    public int getMaxActionsToFlush() {
        return maxActionsToFlush;
    }

    public void setMaxActionsToFlush(int maxActionsToFlush) {
        this.maxActionsToFlush = maxActionsToFlush;
    }

    public long getMinBytesToFlush() {
        return minBytesToFlush;
    }

    public void setMinBytesToFlush(long minBytesToFlush) {
        this.minBytesToFlush = minBytesToFlush;
    }

    public long getMaxBytesToFlush() {
        return maxBytesToFlush;
    }

    public void setMaxBytesToFlush(long maxBytesToFlush) {
        this.maxBytesToFlush = maxBytesToFlush;
    }

    public boolean isAboveMinimum(int actions, long bytes) {
        return actions >= minActionsToFlush && bytes >= minBytesToFlush;
    }

    public boolean isAboveMaximum(int actions, long bytes) {
        return actions >= maxActionsToFlush && bytes >= maxBytesToFlush;
    }

}
