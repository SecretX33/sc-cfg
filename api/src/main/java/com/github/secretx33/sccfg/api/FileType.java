package com.github.secretx33.sccfg.api;

public enum FileType {
    YAML(".yml");

    public final String extension;

    FileType(String extension) {
        this.extension = extension;
    }
}
