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

import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.util.Maps;
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractConfigurateSerializer<U extends AbstractConfigurationLoader.Builder<U, L>, L extends AbstractConfigurationLoader<?>> extends AbstractSerializer {

    public AbstractConfigurateSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    abstract AbstractConfigurationLoader.Builder<U, L> fileBuilder();

    /**
     * Loads a file, transforming the read value from "file names" to "file values" into a map of
     * "java names" to "java values". The keys are transformed fields' {@link ConfigEntry#getName()},
     * and values are transformed by serializing the file values and deserializing them using the
     * {@link ConfigEntry#getGenericType()}.
     *
     * @param configWrapper the config that should have their file read
     * @return a map holding the "java names" mapped to the converted "java values"
     */
    @Override
    protected Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper) {
        final Path filePath = configWrapper.getDestination();
        final ConfigurationNode file;

        try {
            file = fileBuilder().path(filePath).build().load();
        } catch (final ConfigurateException e) {
            logger.log(Level.SEVERE, "An error has occurred when deserializing file '" + configWrapper.getDestination().getFileName() + "' from " + configWrapper.getFileType() + ". There is probably some kind of typo on it, so it could not be parsed, please fix any typos on the file.", new ConfigDeserializationException(e));
            return Collections.emptyMap();
        }

        final Gson gson = gsonFactory.getInstance();
        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();
        final Map<String, Object> values = new LinkedHashMap<>();

        configEntries.forEach(entry -> {
            final Type type = entry.getGenericType();
            final String path = entry.isAtRoot() ? entry.getNameOnFile() : (entry.getPath() + "." + entry.getNameOnFile());
            final ConfigurationNode node = file.node(Arrays.stream(path.split("\\.")).iterator());
            try {
                final Object value = gson.fromJson(gson.toJson(node.raw()), type);
                values.put(entry.getName(), value);
            } catch (final JsonParseException e) {
                throw new ConfigDeserializationException("sc-cfg doesn't know how to deserialize field " + entry.getName() + " in config class '" + entry.getOwnerClass().getName() + "', consider adding a Type Adapter for this field type", e);
            }
        });
        return Maps.immutableOf(values);
    }

    @Override
    protected void saveToFile(final ConfigWrapper<?> configWrapper, final Map<String, Object> newValues) {
        final Path path = configWrapper.getDestination();
        final String json;

        try {
            json = gsonFactory.getInstance().toJson(newValues, linkedMapToken);
        } catch (final Exception e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing config class " + configWrapper.getInstance().getClass().getName(), ex);
            throw ex;
        }

        final ConfigurationNode fileNode;
        try {
            fileNode = fileBuilder().buildAndLoadString(json);
        } catch (final ConfigurateException e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when converting the config class " + configWrapper.getInstance().getClass().getName() + " to " + configWrapper.getFileType() + ".", ex);
            throw ex;
        }

        try {
            fileBuilder().path(path).build().save(fileNode);
        } catch (final ConfigurateException e) {
            logger.log(Level.SEVERE, "An error has occurred when saving your config file '" + configWrapper.getInstance().getClass().getName() + " to the disk.", e);
            throw new ConfigException(e);
        }
    }
}
