package com.github.euler.config;

import com.github.euler.core.Task;
import com.typesafe.config.Config;

public interface TaskCreator {

    String type();

    Task create(Config config, TaskFactory taskFactory, ConfigContext ctx);

}
