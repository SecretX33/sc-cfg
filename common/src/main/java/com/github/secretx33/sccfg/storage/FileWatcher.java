/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.github.secretx33.sccfg.storage;

import com.github.secretx33.sccfg.util.ExpiringMap;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Simple implementation of {@link AbstractFileWatcher}.
 */
public final class FileWatcher extends AbstractFileWatcher {

    /** The base watched path */
    private final Path basePath;

    /** A map of watched locations with corresponding listeners */
    private final Map<Path, WatchedLocation> watchedLocations = new ConcurrentHashMap<>();

    public FileWatcher(final Path basePath) {
        super(checkNotNull(basePath, "basePath cannot be null")
                .getFileSystem(), true);
        basePath.toFile().mkdirs();
        checkArgument(Files.exists(basePath), "basePath needs to exist");
        checkArgument(Files.isDirectory(basePath), "basePath needs to be a directory");
        this.basePath = basePath;
        super.registerRecursively(basePath);
        super.runEventProcessingLoopAsync();
    }

    /**
     * Gets a {@link WatchedLocation} instance for a given path.
     *
     * @param path the path to get a watcher for
     * @return the watched location
     */
    public WatchedLocation getWatcher(Path path) {
        checkNotNull(path);
        if (path.isAbsolute()) {
            path = basePath.relativize(path);
        } else if(path.startsWith(basePath)) {
            path = basePath.relativize(path.toAbsolutePath());
        }
        return watchedLocations.computeIfAbsent(path, WatchedLocation::new);
    }

    @Override
    protected void processEvent(final FileWatcherEvent event) {
        // return if there's no element
        if (event.getFile().getNameCount() == 0) {
            return;
        }

        // pass the event onto all watched locations that match
        watchedLocations.entrySet().stream()
            .filter(entry -> event.getFile().startsWith(basePath.resolve(entry.getKey()).toAbsolutePath()))
            .map(Map.Entry::getValue)
            .forEach(watchedLocation -> watchedLocation.onEvent(event));
    }

    /**
     * Encapsulates a "watcher" in a specific directory.
     */
    public static final class WatchedLocation {

        private final Path basePath;

        /** A set of files which have been modified recently */
        private final ExpiringMap<UUID, Path> recentlyConsumedFiles = new ExpiringMap<>(1, TimeUnit.SECONDS);

        /** The listener callback functions */
        private final List<FileWatcherEventConsumer> callbacks = new CopyOnWriteArrayList<>();

        WatchedLocation(final Path basePath) {
            this.basePath = checkNotNull(basePath);
        }

        void onEvent(final FileWatcherEvent event) {
            // pass the event onto registered listeners
            callbacks.stream()
                .filter(cb -> cb.getAcceptTypes().contains(event.getType()))
                .filter(cb -> recentlyConsumedFiles.put(cb.getUniqueId(), basePath.relativize(event.getFile())))
                .forEach(cb -> {
                    try {
                        cb.accept(event);
                    } catch (final Exception e) {
                        e.printStackTrace();
                    }
                });
        }

        /**
         * Record that a file has been changed recently.
         *
         * @param path the path of the file
         */
        public void recordChange(final Path path) {
            final Map<UUID, Path> recentlyConsumed = new HashMap<>();
            callbacks.forEach(cb -> recentlyConsumed.put(cb.getUniqueId(), path));
            recentlyConsumedFiles.putAll(recentlyConsumed);
        }

        /**
         * Register a listener.
         *
         * @param listener the listener
         */
        public void addListener(Set<FileModificationType> modificationTypes, Consumer<FileWatcherEvent>  listener) {
            callbacks.add(new FileWatcherEventConsumer(listener, modificationTypes));
        }
    }

}
