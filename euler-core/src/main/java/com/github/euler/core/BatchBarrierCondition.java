package com.github.euler.core;

import java.util.List;

public interface BatchBarrierCondition {

    List<Boolean> block(List<JobTaskToProcess> msgs);

}