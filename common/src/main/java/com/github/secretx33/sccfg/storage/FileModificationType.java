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

import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.util.Set;

public enum FileModificationType {

    /**
     * Triggered when a new entry is made in the watched directory. It could be due to the creation of a new file or renaming of an existing file.
     */
    CREATE,

    /**
     * Triggered when an existing entry in the watched directory is modified. All file edit's trigger this event. On some platforms, even changing file attributes will trigger it.
     */
    MODIFY,

    /**
     * Triggered when an entry is deleted, moved or renamed in the watched directory.
     */
    DELETE,

    /**
     * Triggered to indicate lost or discarded events. Unless you know you need this, you can safely ignore this entry.
     */
    OVERFLOW;

    public boolean isCreate() {
        return this == CREATE;
    }

    public boolean isModify() {
        return this == CREATE;
    }

    public boolean isCreateOrModify() {
        return this == CREATE || this == MODIFY;
    }

    public static final Set<FileModificationType> CREATE_AND_MODIFICATION = Sets.immutableOf(CREATE, MODIFY);

    public static FileModificationType adapt(final WatchEvent<Path> event) {
        if(event.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
            return FileModificationType.CREATE;
        if(event.kind().equals(StandardWatchEventKinds.ENTRY_MODIFY))
            return FileModificationType.MODIFY;
        if(event.kind().equals(StandardWatchEventKinds.ENTRY_DELETE))
            return FileModificationType.DELETE;
        if(event.kind().equals(StandardWatchEventKinds.OVERFLOW))
            return FileModificationType.OVERFLOW;
        throw new IllegalStateException("Could not convert unknown event " + event.kind().toString() + " to a FileModificationType");
    }
}
