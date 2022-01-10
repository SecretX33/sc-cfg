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
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.exception.MissingSerializerDependency;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;

import java.lang.reflect.Constructor;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Map<FileType, Serializer> serializers = new EnumMap<>(FileType.class);
    private final Logger logger;
    private final GsonFactory gsonFactory;

    public SerializerFactory(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    public Serializer getSerializer(final FileType fileType) {
        checkNotNull(fileType, "fileType");
        return serializers.computeIfAbsent(fileType, this::serializerFor);
    }

    private Serializer serializerFor(final FileType fileType) {
        checkNotNull(fileType, "fileType");

        final String className = getClass().getPackage().getName() + "." + fileType.getClassName();
        try {
            final Constructor<?> constructor = Class.forName(className).getDeclaredConstructor(Logger.class, GsonFactory.class);
            constructor.setAccessible(true);
            return (Serializer) constructor.newInstance(logger, gsonFactory);
        } catch (final ClassNotFoundException e) {
            final MissingSerializerDependency ex = new MissingSerializerDependency(fileType, e);
            logger.log(Level.SEVERE, "Could not create a serializer for type " + fileType + " (" + fileType.getExtension() + ")", ex);
            throw ex;
        } catch (final ClassCastException | ReflectiveOperationException e) {
            throw new ConfigReflectiveOperationException("If you are reading this, it means that sc-cfg was not able to instantiate serializer class of file type " + fileType + ", and that there's a problem with sc-cfg, please report this!", e);
        }
    }
}
