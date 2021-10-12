package com.github.secretx33.sccfg.storage;

import java.nio.file.Path;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class FileWatcherEvent {

    private final Path file;
    private final FileModificationType type;

    public FileWatcherEvent(final Path file, final FileModificationType type) {
        this.file = checkNotNull(file);
        this.type = checkNotNull(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileWatcherEvent that = (FileWatcherEvent) o;
        return file.equals(that.file) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, type);
    }

    @Override
    public String toString() {
        return "FileWatcherEvent{" +
                "file=" + file +
                ", type=" + type +
                '}';
    }
}
