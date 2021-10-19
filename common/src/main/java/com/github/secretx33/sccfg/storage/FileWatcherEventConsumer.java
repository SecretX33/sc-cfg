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
        this.consumer = checkNotNull(consumer, "consumer");
    }

    public FileWatcherEventConsumer(final Consumer<FileWatcherEvent> consumer, final Set<FileModificationType> acceptTypes) {
        checkNotNull(acceptTypes, "acceptTypes");
        checkArgument(!acceptTypes.isEmpty(), "cannot listen to zero modification types");
        this.acceptTypes = acceptTypes;
        this.consumer = checkNotNull(consumer, "consumer");
    }

    public void accept(final FileWatcherEvent event) {
        consumer.accept(checkNotNull(event, "event"));
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
