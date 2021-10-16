package com.github.secretx33.sccfg.storage;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class FileWatcherProvider {

    private static final Map<Path, FileWatcher> watchers = new ConcurrentHashMap<>();

    private FileWatcherProvider() {}

    public static FileWatcher get(final Path basePath) {
        checkNotNull(basePath, "basePath");
        return watchers.computeIfAbsent(basePath, FileWatcher::new);
    }
}
