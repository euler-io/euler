package com.github.euler.core;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import akka.actor.testkit.typed.javadsl.ActorTestKit;

public abstract class AkkaTest {

    protected static ActorTestKit testKit;

    @BeforeClass
    public static void setup() {
        testKit = ActorTestKit.create();
    }

    @AfterClass
    public static void teardown() {
        testKit.shutdownTestKit();
    }

}
