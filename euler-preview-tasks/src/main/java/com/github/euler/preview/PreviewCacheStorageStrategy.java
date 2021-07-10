package com.github.euler.preview;

import java.io.File;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;

import com.github.euler.common.StorageStrategy;
import com.github.euler.file.FileUtils;

public class PreviewCacheStorageStrategy implements StorageStrategy {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\$\\{(\\w+)\\}");

    private final File root;
    private final String suffix;
    private final int width;
    private final int height;
    private final String format;

    public PreviewCacheStorageStrategy(File root, String suffix, int width, int height, String format) {
        super();
        this.root = root;
        this.suffix = suffix;
        this.width = width;
        this.height = height;
        this.format = format;
    }

    @Override
    public URI createFile(URI uri) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        String hash = DigestUtils.md5Hex(FileUtils.toFile(uri).getAbsolutePath()).toLowerCase();
        return createFile(baseName, hash, this.suffix);
    }

    @Override
    public URI createFile(String suffix) {
        String baseName = UUID.randomUUID().toString();
        String hash = DigestUtils.md5Hex(baseName).toLowerCase();
        return createFile(baseName, hash, suffix);
    }

    @Override
    public URI createFile(URI uri, String suffix) {
        String baseName = FilenameUtils.getBaseName(FileUtils.toFile(uri).getAbsolutePath());
        String hash = DigestUtils.md5Hex(FileUtils.toFile(uri).getAbsolutePath()).toLowerCase();
        return createFile(baseName, hash, suffix);
    }

    public URI createFile(String baseName, String hash, String suffix) {
        String fileName = format(baseName, hash, this.suffix);
        File dir = new File(root, fileName);
        dir.getParentFile().mkdirs();
        return dir.toURI();
    }

    protected String format(String fileName, String hash, String extension) {
        Map<String, String> params = Map.of("fileName", fileName,
                "hash", hash,
                "height", Integer.toString(height),
                "width", Integer.toString(width),
                "page", "0",
                "extension", extension);
        return PLACEHOLDER_PATTERN.matcher(format).replaceAll(r -> {
            String key = r.group(1);
            return params.getOrDefault(key, "undefined");
        });
    }

    @Override
    public URI createFile() {
        return createFile(this.suffix);
    }

    @Override
    public URI createFileWithName(String name) {
        File dir = new File(root, name);
        dir.mkdirs();
        return dir.toURI();
    }

}
