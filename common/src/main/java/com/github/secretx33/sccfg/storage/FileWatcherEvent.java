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
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class FileWatcherEvent {

    private final Path file;
    private final FileModificationType type;

    public FileWatcherEvent(final Path file, final FileModificationType type) {
        this.file = checkNotNull(file, "file");
        this.type = checkNotNull(type, "type");
    }

    public Path getFile() {
        return file;
    }

    public FileModificationType getType() {
        return type;
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
