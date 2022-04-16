/*
 * Copyright (C) 2021-2022 SecretX <notyetmidnight@gmail.com>
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
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.exception.MissingSerializerDependency;

import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Logger logger;
    private final GsonProvider gsonProvider;

    public SerializerFactory(final Logger logger, final GsonProvider gsonProvider) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonProvider = checkNotNull(gsonProvider, "gsonProvider");
    }

    public Serializer getSerializer(final FileType fileType) {
        checkNotNull(fileType, "fileType");
        return createSerializer(fileType);
    }

    private Serializer createSerializer(final FileType fileType) {
        try {
            switch(fileType) {
                case HOCON:
                    return new HoconSerializer(logger, gsonProvider);
                case JSON:
                    return new JsonSerializer(logger, gsonProvider);
                case YAML:
                    return new YamlSerializer(logger, gsonProvider);
                default:
                    throw new IllegalStateException("Could not found a valid serializer for file type " + fileType);
            }
        } catch (final NoClassDefFoundError e) {
            throw new MissingSerializerDependency(fileType, e);
        }
    }
}
