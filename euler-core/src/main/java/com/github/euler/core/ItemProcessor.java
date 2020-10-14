package com.github.euler.core;

import java.io.IOException;

public interface ItemProcessor {

    ProcessingContext process(Item item) throws IOException;

}