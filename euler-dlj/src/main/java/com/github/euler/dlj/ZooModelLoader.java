package com.github.euler.dlj;

import java.io.IOException;

import ai.djl.MalformedModelException;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;

public interface ZooModelLoader<I, O> {

    ZooModel<I, O> load() throws IOException, ModelNotFoundException, MalformedModelException;

}
