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
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;
import com.github.secretx33.sccfg.exception.ConfigOverlappingPath;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.util.Maps;
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.google.gson.Gson;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractConfigurateSerializer<U extends AbstractConfigurationLoader.Builder<U, L>, L extends AbstractConfigurationLoader<?>> extends AbstractSerializer {

    public AbstractConfigurateSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    abstract AbstractConfigurationLoader.Builder<U, L> fileBuilder();

    /**
     * Created a new, empty node compatible with the {@code <L>} configuration loader.
     *
     * @return An empty configuration node
     */
    protected final ConfigurationNode emptyNode() {
        return fileBuilder().build().createNode();
    }

    /**
     * Loads a file, transforming the read value from "file names" to "file values" into a map of
     * "java names" to "file values". The keys are transformed fields' {@link ConfigEntry#getName()},
     * and values are kept intact (since they'll be converted only if needed when setting them into
     * the config field).
     *
     * @param configWrapper the config that should have its file read
     * @return a map holding the "java names" mapped to "file values"
     */
    @Override
    protected final Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper) {
        final Path filePath = configWrapper.getDestination();
        final ConfigurationNode file;

        try {
            file = fileBuilder().path(filePath).build().load();
        } catch (final ConfigurateException e) {
            logger.log(Level.SEVERE, "An error has occurred when deserializing file '" + configWrapper.getDestination().getFileName() + "' from " + configWrapper.getFileType() + ". There is probably some kind of typo on it, so it could not be parsed, please fix any typos on the file.", new ConfigDeserializationException(e));
            return Collections.emptyMap();
        }

        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();
        final Map<String, Object> values = new LinkedHashMap<>();

        configEntries.forEach(entry -> {
            final String pathOnFile = entry.getFullPathOnFile();
            final Object value = file.node(Arrays.asList(pathOnFile.split("\\."))).raw();
            if (value != null) {
                values.put(entry.getName(), value);
            }
        });
        return Maps.immutableOf(values);
    }

    protected String convertValuesMapToSerializedFile(final Map<String, Object> valuesMap) throws ConfigurateException {
        return gsonFactory.getInstance().toJson(valuesMap, linkedMapToken);
    }

    @Override
    protected final void saveToFile(final ConfigWrapper<?> configWrapper, final Map<String, Object> newValues) {
        final Path path = configWrapper.getDestination();
        final String serializedFile;

        try {
            serializedFile = convertValuesMapToSerializedFile(newValues);
        } catch (final ConfigurateException e) {
            // probably consumer's fault, some invalid character or something on the config class contents
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing values of class '" + configWrapper.getInstance().getClass().getName() + "', maybe there is some kind of invalid value on some string inside your config class? Read the nested exception for more details.", e);
            throw ex;
        } catch (final Exception e) {
            // something went wrong internally
            final ConfigInternalErrorException ex = new ConfigInternalErrorException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing newValues map (which had values from config class '" + configWrapper.getInstance().getClass().getName() + "'), and that should not happen because this map should only contains serializable values, if you are seeing this, please report this error in sc-cfg github so we can fix it!", ex);
            throw ex;
        }

        final ConfigurationNode fileNode;
        try {
            fileNode = fileBuilder().buildAndLoadString(serializedFile);
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

    @Override
    @SuppressWarnings("unchecked")
    public final Map<String, Object> getCurrentValues(final Object configInstance, final Set<ConfigEntry> configEntries) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(configEntries, "configEntries");

        final ConfigurationNode root = emptyNode();
        final Gson gson = gsonFactory.getInstance();

        configEntries.forEach(configEntry -> {
            final String pathOnFile = configEntry.getFullPathOnFile();
            final Object serializableValue;

            try {
                final Type fieldType = configEntry.getGenericType();
                serializableValue = gson.fromJson(gson.toJson(configEntry.get(), fieldType), Object.class);
            } catch (final RuntimeException e) {
                throw new ConfigSerializationException("sc-cfg doesn't know how to serialize field '" + configEntry.getName() + "' in config class '" + configInstance.getClass().getName() + "', consider adding a Type Adapter for " + configEntry.getGenericType() + ".", e);
            }

            final ConfigurationNode node = root.node(Arrays.asList(pathOnFile.split("\\.")));

            if (!node.isNull()) {
                throw new ConfigOverlappingPath("There is an overlapping config on key '" + configEntry.getPathOnFile() + "' of config instance of class " + configInstance.getClass().getSimpleName() + ", which prevented the serialization of field '" + configEntry.getName() + "'. Please structure your paths in a way that ensure that there is no possibility of collision between two properties.");
            }

            try {
                node.set(serializableValue);
            } catch (final SerializationException e) {
                // should not be thrown
                throw new ConfigInternalErrorException("configurate could not serialize value " + serializableValue + " (class " + serializableValue.getClass() + ")" + ", which should not happen because Gson should already taken care of serializing this class to something serializable", e);
            }
        });

        return (root.raw() instanceof Map<?, ?>) ? Maps.immutableOf((Map<String, Object>)root.raw()) : Collections.emptyMap();
    }
}
