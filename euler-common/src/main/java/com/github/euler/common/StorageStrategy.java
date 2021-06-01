package com.github.euler.common;

import java.net.URI;

public interface StorageStrategy {

    URI createFile();

    URI createFile(URI uri);

    URI createFile(URI uri, String suffix);

    URI createFile(String suffix);

}
