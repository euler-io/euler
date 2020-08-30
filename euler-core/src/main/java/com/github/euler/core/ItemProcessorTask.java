package com.github.euler.core;

public class ItemProcessorTask extends AbstractTask {

    private final ItemProcessor itemProcessor;

    public ItemProcessorTask(String name, ItemProcessor itemProcessor) {
        super(name);
        this.itemProcessor = itemProcessor;
    }

    @Override
    protected ItemProcessor itemProcessor() {
        return this.itemProcessor;
    }

}
