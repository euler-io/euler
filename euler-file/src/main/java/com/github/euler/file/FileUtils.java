package com.github.euler.file;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private FileUtils() {
        super();
    }

    public static String getRelativePath(File parent, File file) {
        return parent.getName() + file.getAbsolutePath().replaceFirst(parent.getAbsolutePath(), "");
    }

    public static Path toPath(URI uri) {
        try {
            URI fileURI = new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), null, null);
            return Paths.get(fileURI).normalize();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static File toFile(URI uri) {
        return toPath(uri).toFile();
    }

}
