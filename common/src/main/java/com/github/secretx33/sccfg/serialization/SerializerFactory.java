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
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;
import com.github.secretx33.sccfg.exception.MissingSerializerDependency;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;

import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Logger logger;
    private final GsonFactory gsonFactory;

    public SerializerFactory(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    public Serializer getSerializer(final FileType fileType) {
        checkNotNull(fileType, "fileType");
        return createSerializer(fileType);
    }

    private Serializer createSerializer(final FileType fileType) {
        try {
            switch(fileType) {
                case HOCON:
                    return new HoconSerializer(logger, gsonFactory);
                case JSON:
                    return new JsonSerializer(logger, gsonFactory);
                case YAML:
                    return new YamlSerializer(logger, gsonFactory);
                default:
                    throw new ConfigInternalErrorException("If you are reading this, it means that sc-cfg doesn't have a registered serializer for type " + fileType + ", and that there's a problem with sc-cfg, please report this!");
            }
        } catch (final NoClassDefFoundError e) {
            throw new MissingSerializerDependency(fileType, e);
        }
    }
}
