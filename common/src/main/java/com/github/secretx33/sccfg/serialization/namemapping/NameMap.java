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
