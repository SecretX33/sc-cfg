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

import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public abstract class AbstractFileWatcher implements AutoCloseable  {

    /** The watch service  */
    private final WatchService watchService;

    /** A map of all registered watch keys  */
    private final Map<WatchKey, Path> keys = new ConcurrentHashMap<>();

    /** If this file watcher should discover directories */
    private final boolean autoRegisterNewSubDirectories;

    /** The thread currently being used to wait for & process watch events */
    private final AtomicReference<Thread> processingThread = new AtomicReference<>();

    /**
     * The Coroutine Thread that will be used only to monitor changes of this folder
     */
    private final Executor singleThreadExecutor = Executors.newSingleThreadExecutor();

    public AbstractFileWatcher(
        final FileSystem fileSystem,
        final boolean autoRegisterNewSubDirectories
    ) {
        checkNotNull(fileSystem, "fileSystem");
        try {
            this.watchService = checkNotNull(fileSystem.newWatchService(), "fileSystem.newWatchService()");
            this.autoRegisterNewSubDirectories = autoRegisterNewSubDirectories;
        } catch (final IOException e) {
            throw new ConfigException(e);
        }
    }

    /**
     * Get a {@link WatchKey} from the given {@link WatchService} in the given {@link Path directory}.
     *
     * @param watchService the watch service
     * @param directory the directory
     * @return the watch key
     * @throws ConfigException if unable to register
     */
    private static WatchKey register(WatchService watchService, Path directory) {
        try {
            return directory.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (final IOException e) {
            throw new ConfigException(e);
        }
    }

    /**
     * Register a watch key in the given directory.
     *
     * @param directory the directory
     */
    public void register(final Path directory) {
        checkNotNull(directory, "directory");
        final WatchKey key = register(watchService, directory);
        keys.put(key, directory);
    }

    /**
     * Register a watch key recursively in the given directory.
     *
     * @param root the root directory
     * @throws ConfigException if unable to register a key
     */
    public void registerRecursively(Path root) {
        try {
            Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    register(dir);
                    return super.preVisitDirectory(dir, attrs);
                }
            });
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process an observed watch event.
     *
     * @param event the event
     */
    protected abstract void processEvent(final FileWatcherEvent event);

    protected final void runEventProcessingLoopAsync() {
        CompletableFuture.runAsync(this::runEventProcessingLoop, singleThreadExecutor);
    }

    /**
     * Processes {@link WatchEvent}s from the watch service until it is closed, or until
     * the thread is interrupted.
     */
    private void runEventProcessingLoop() {
        if (!processingThread.compareAndSet(null, Thread.currentThread())) {
            throw new ConfigInternalErrorException("A thread is already processing events for this watcher.");
        }

        while (true) {
            // poll for a key from the watch service
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException | ClosedWatchServiceException e) {
                break;
            }

            // find the directory the key is watching
            Path directory = keys.get(key);
            if (directory == null) {
                key.cancel();
                continue;
            }

            // process each watch event the key has
            for (WatchEvent<?> ev : key.pollEvents()) {
                @SuppressWarnings("unchecked")
                WatchEvent<Path> event = (WatchEvent<Path>) ev;

                Path context = event.context();

                // ignore contexts with a name count of zero
                if (context == null || context.getNameCount() == 0) {
                    continue;
                }

                // resolve the context of the event against the directory being watched
                Path file = directory.resolve(context);

                // if the file is a regular file, send the event on to be processed
                if (Files.isRegularFile(file)) {
                    processEvent(new FileWatcherEvent(file, FileModificationType.adapt(event)));
                }

                // handle recursive directory creation
                if (autoRegisterNewSubDirectories
                        && event.kind() == StandardWatchEventKinds.ENTRY_CREATE
                        && Files.isDirectory(file, LinkOption.NOFOLLOW_LINKS)) {
                    registerRecursively(file);
                }
            }

            // reset the key
            final boolean valid = key.reset();
            if (!valid) {
                keys.remove(key);
            }
        }

        this.processingThread.compareAndSet(Thread.currentThread(), null);
    }

    @Override
    public void close() {
        try {
            watchService.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
