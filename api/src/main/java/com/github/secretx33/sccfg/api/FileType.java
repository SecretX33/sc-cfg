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
package com.github.secretx33.sccfg.api;

/**
 * The type the config file will be.
 */
public enum FileType {
    HOCON(".conf", "HoconSerializer"),
    JSON(".json", "JsonSerializer"),
    YAML(".yml", "YamlSerializer");

    private final String extension;
    private final String className;

    FileType(final String extension, final String className) {
        this.extension = extension;
        this.className = className;
    }

    public String getExtension() {
        return extension;
    }

    public String getClassName() {
        return className;
    }
}
