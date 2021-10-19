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
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Map<FileType, Serializer> serializers = new EnumMap<>(FileType.class);
    private final Logger logger;
    private final GsonFactory gsonFactory;
    private final NameMapper nameMapper = new NameMapper();

    public SerializerFactory(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    public Serializer getFor(final FileType fileType) {
        checkNotNull(fileType, "fileType");
        return getSerializer(fileType);
    }

    private Serializer getSerializer(final FileType fileType) {
        switch(fileType) {
            case HOCON:
                return serializers.computeIfAbsent(fileType, type -> new HoconSerializer(logger, gsonFactory));
            case JSON:
                return serializers.computeIfAbsent(fileType, type -> new JsonSerializer(logger, gsonFactory));
            case YAML:
                return serializers.computeIfAbsent(fileType, type -> new YamlSerializer(logger, gsonFactory));
            default:
                throw new IllegalStateException("Oops, I don't have a registered serializer for " + fileType + " type, what a shame!");
        }
    }

    public NameMapper getNameMapper() {
        return nameMapper;
    }
}
