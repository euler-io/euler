package com.github.euler.file;

import java.net.URI;
import java.nio.file.Path;

import com.github.euler.core.BarrierCondition;
import com.github.euler.core.JobTaskToProcess;

public class ExistsInFileSystemCondition implements BarrierCondition {

    @Override
    public boolean block(JobTaskToProcess msg) {
        URI uri = msg.itemURI;
        Path path = FileUtils.toPath(uri);
        return path.toFile().exists();
    }

}
