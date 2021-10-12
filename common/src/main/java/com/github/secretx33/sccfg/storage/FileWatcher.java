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

import com.github.secretx33.sccfg.util.ExpiringSet;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Simple implementation of {@link AbstractFileWatcher} for LuckPerms data files.
 */
public class FileWatcher extends AbstractFileWatcher {

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
    protected void processEvent(WatchEvent<Path> event, Path path) {
        // get the relative path of the event
        Path relativePath = this.basePath.relativize(path);
        if (relativePath.getNameCount() == 0) {
            return;
        }

        // pass the event onto all watched locations that match
        for (Map.Entry<Path, WatchedLocation> entry : this.watchedLocations.entrySet()) {
            if (relativePath.startsWith(entry.getKey())) {
                entry.getValue().onEvent(event, relativePath);
            }
        }
    }

    /**
     * Encapsulates a "watcher" in a specific directory.
     */
    public static final class WatchedLocation {
        /** The directory being watched by this instance. */
        private final Path path;

        /** A set of files which have been modified recently */
        private final ExpiringSet<String> recentlyModifiedFiles = new ExpiringSet<>(1, TimeUnit.SECONDS);

        /** The listener callback functions */
        private final List<Consumer<Path>> callbacks = new CopyOnWriteArrayList<>();

        WatchedLocation(Path path) {
            this.path = path;
        }

        void onEvent(WatchEvent<Path> event, Path path) {
            // get the relative path of the modified file
            Path relativePath = path.relativize(path);

            // check if the file has been modified recently
            String fileName = relativePath.toString();
            if (!recentlyModifiedFiles.add(fileName)) {
                return;
            }

            // pass the event onto registered listeners
            callbacks.forEach(cb -> {
                try {
                    cb.accept(relativePath);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            });
        }

        /**
         * Record that a file has been changed recently.
         *
         * @param fileName the name of the file
         */
        public void recordChange(String fileName) {
            recentlyModifiedFiles.add(fileName);
        }

        /**
         * Register a listener.
         *
         * @param listener the listener
         */
        public void addListener(Consumer<Path> listener) {
            callbacks.add(listener);
        }
    }

}
