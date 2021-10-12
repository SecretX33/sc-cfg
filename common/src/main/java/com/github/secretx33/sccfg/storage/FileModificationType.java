package com.github.secretx33.sccfg.storage;

import com.github.secretx33.sccfg.util.Sets;

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
}
