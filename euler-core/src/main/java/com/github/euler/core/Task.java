package com.github.euler.core;

import com.github.euler.message.EvidenceItemToProcess;

import akka.actor.typed.Behavior;

public interface Task {

    String name();

    Behavior<EvidenceItemToProcess> behavior();

}
