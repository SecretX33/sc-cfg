package com.github.secretx33.sccfg.api;

/**
 * The type the config file will be.
 */
public enum FileType {
    JSON(".json"),
    YAML(".yml");

    public final String extension;

    FileType(String extension) {
        this.extension = extension;
    }
}
