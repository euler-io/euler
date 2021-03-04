package com.github.euler.core;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Predicate;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class TaskBuilder {

    private String name;

    private Predicate<Item> accept = new Predicate<Item>() {

        @Override
        public boolean test(Item t) {
            return true;
        }

    };

    private boolean flushable = false;

    private ItemProcessor processor = ItemProcessor.VOID;

    private Duration timeout = Duration.ZERO;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TaskBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Predicate<Item> getAccept() {
        return accept;
    }

    public void setAccept(Predicate<Item> accept) {
        this.accept = accept;
    }

    public TaskBuilder accept(Predicate<Item> accept) {
        this.accept = accept;
        return this;
    }

    public boolean isFlushable() {
        return flushable;
    }

    public void setFlushable(boolean flushable) {
        this.flushable = flushable;
    }

    public TaskBuilder flushable(boolean flushable) {
        this.flushable = flushable;
        return this;
    }

    public ItemProcessor getProcessor() {
        return processor;
    }

    public void setProcessor(ItemProcessor processor) {
        this.processor = processor;
    }

    public TaskBuilder processor(ItemProcessor processor) {
        this.processor = processor;
        return this;
    }

    public Duration getTimeout() {
        return timeout;
    }

    public void setTimeout(Duration timeout) {
        this.timeout = timeout;
    }

    public TaskBuilder timeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public Task build() {
        Objects.requireNonNull(name, "name can't be null");
        Objects.requireNonNull(name, "accept can't be null");
        Objects.requireNonNull(name, "flushable can't be null");
        Objects.requireNonNull(name, "processor can't be null");
        Objects.requireNonNull(name, "timeout can't be null");
        return new BuildedTask(name, accept, flushable, processor, timeout);
    }

    static class BuildedTask implements Task {

        private final String name;
        private final Predicate<Item> accept;
        private final boolean flushable;
        private final ItemProcessor processor;
        private final Duration timeout;

        BuildedTask(String name, Predicate<Item> accept, boolean flushable, ItemProcessor processor, Duration timeout) {
            super();
            this.name = name;
            this.accept = accept;
            this.flushable = flushable;
            this.processor = processor;
            this.timeout = timeout;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public Behavior<TaskCommand> behavior() {
            return Behaviors.setup((ctx) -> new ItemProcessorExecution(ctx, processor));
        }

        @Override
        public boolean accept(JobTaskToProcess msg) {
            return accept.test(new Item(msg));
        }

        @Override
        public boolean isFlushable() {
            return flushable;
        }

        @Override
        public Duration getTimeout() {
            return timeout;
        }

    }

}
