package com.github.euler.core;

public interface BarrierCondition {

    boolean block(JobTaskToProcess msg);

}
