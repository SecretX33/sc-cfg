package com.github.secretx33.sccfg.storage;

import com.github.secretx33.sccfg.util.Sets;

import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class FileWatcherEventConsumer {

    private final Set<FileModificationType> acceptTypes;
    private final Consumer<FileWatcherEvent> consumer;
    private final UUID uniqueId = UUID.randomUUID();

    public FileWatcherEventConsumer(final Consumer<FileWatcherEvent> consumer, final FileModificationType... acceptTypes) {
        checkNotNull(acceptTypes, "acceptTypes");
        checkArgument(acceptTypes.length > 0, "cannot listen to zero modification types");
        this.acceptTypes = Sets.immutableOf(acceptTypes);
        this.consumer = checkNotNull(consumer);
    }

    public FileWatcherEventConsumer(final Consumer<FileWatcherEvent> consumer, final Set<FileModificationType> acceptTypes) {
        checkNotNull(acceptTypes, "acceptTypes");
        checkArgument(!acceptTypes.isEmpty(), "cannot listen to zero modification types");
        this.acceptTypes = acceptTypes;
        this.consumer = checkNotNull(consumer);
    }

    public void accept(final FileWatcherEvent event) {
        consumer.accept(checkNotNull(event));
    }

    public Set<FileModificationType> getAcceptTypes() {
        return acceptTypes;
    }

    public Consumer<FileWatcherEvent> getConsumer() {
        return consumer;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return "FileWatcherEventConsumer{" +
                "acceptTypes=" + acceptTypes +
                ", consumer=" + consumer +
                '}';
    }
}
