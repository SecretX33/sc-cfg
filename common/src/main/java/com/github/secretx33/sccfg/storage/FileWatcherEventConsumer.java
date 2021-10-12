package com.github.secretx33.sccfg.storage;

import com.github.secretx33.sccfg.util.Sets;

import java.util.Set;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class FileWatcherEventConsumer {

    private final Set<FileModificationType> acceptTypes;
    private final Consumer<FileWatcherEvent> consumer;

    public FileWatcherEventConsumer(final Consumer<FileWatcherEvent> consumer, final FileModificationType... acceptTypes) {
        checkNotNull(acceptTypes, "acceptTypes cannot be null");
        checkArgument(acceptTypes.length > 0, "cannot listen to zero modification types");
        this.acceptTypes = Sets.immutableOf(acceptTypes);
        this.consumer = checkNotNull(consumer);
    }
    
    public FileWatcherEventConsumer(final Consumer<FileWatcherEvent> consumer, final Set<FileModificationType> acceptTypes) {
        checkNotNull(acceptTypes, "acceptTypes cannot be null");
        checkArgument(!acceptTypes.isEmpty(), "cannot listen to zero modification types");
        this.acceptTypes = acceptTypes;
        this.consumer = checkNotNull(consumer);
    }

    @Override
    public String toString() {
        return "FileWatcherEventConsumer{" +
                "acceptTypes=" + acceptTypes +
                ", consumer=" + consumer +
                '}';
    }
}
