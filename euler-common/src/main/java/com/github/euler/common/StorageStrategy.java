package com.github.euler.common;

import java.net.URI;

public interface StorageStrategy {

    URI createFile(URI uri);

    URI createFile(String suffix);

}
