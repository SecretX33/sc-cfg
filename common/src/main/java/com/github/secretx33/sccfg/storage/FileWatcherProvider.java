/*
 * Copyright (C) 2021 SecretX <notyetmidnight@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
