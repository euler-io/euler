package com.github.euler.file;

import java.io.File;
import java.io.IOException;

import com.github.euler.core.Initializable;

public class PathCreate implements Initializable {

    private final String path;

    public PathCreate(String path) {
        super();
        this.path = path;
    }

    @Override
    public void initialize() throws IOException {
        File file = new File(path);
        file.mkdirs();
    }

}
