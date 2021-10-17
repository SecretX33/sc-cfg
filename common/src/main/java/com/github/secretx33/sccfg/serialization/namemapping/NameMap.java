package com.github.secretx33.sccfg.serialization.namemapping;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class NameMap {

    private final Map<String, String> javaToFileNames;
    private final Map<String, String> fileToJavaNames;

    public NameMap(final Map<String, String> javaToFileNames, final Map<String, String> fileToJavaNames) {
        this.javaToFileNames = checkNotNull(javaToFileNames, "javaToFileNames");
        this.fileToJavaNames = checkNotNull(fileToJavaNames, "fileToJavaNames");
    }

    @Nullable
    public String getJavaEquivalent(final String fileName) {
        return fileToJavaNames.get(fileName);
    }

    @Nullable
    public String getFileEquivalent(final String javaName) {
        return javaToFileNames.get(javaName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameMap nameMap = (NameMap) o;
        return javaToFileNames.equals(nameMap.javaToFileNames) && fileToJavaNames.equals(nameMap.fileToJavaNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(javaToFileNames, fileToJavaNames);
    }

    @Override
    public String toString() {
        return "NameMap{" +
                "javaToFileNames=" + javaToFileNames +
                ", fileToJavaNames=" + fileToJavaNames +
                '}';
    }
}
