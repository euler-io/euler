package com.github.euler.configuration;

import com.github.euler.core.EulerHooks;
import com.github.euler.core.ProcessorCommand;
import com.github.euler.core.SourceCommand;
import com.github.euler.core.source.SourceNotificationStrategy;

import akka.actor.typed.Behavior;

public interface EulerCreator<R> {

    R create(Behavior<SourceCommand> source, Behavior<ProcessorCommand> processor, EulerHooks hooks, SourceNotificationStrategy sourceNotificationStrategy);

}
