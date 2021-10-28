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

import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.util.Maps;
import com.github.secretx33.sccfg.util.Pair;
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractSerializer implements Serializer {

    protected final Logger logger;
    protected final GsonFactory gsonFactory;

    public AbstractSerializer(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    @Override
    public <T> ConfigWrapper<T> loadConfig(final ConfigWrapper<T> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        saveDefault(configWrapper);
        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();
        final Map<String, Object> fileValues = mapFileValuesToJavaValues(configWrapper, loadFromFile(configWrapper));

        configEntries.stream()
            .filter(configEntry -> fileValues.containsKey(configEntry.getName()))
            .forEach(configEntry -> {
                final Object newValue = fileValues.get(configEntry.getName());
                try {
                    setValueOnField(configEntry, newValue);
                } catch (final IllegalArgumentException | JsonSyntaxException e) {
                    // field type does not match the value deserialized
                    final String msg = "Could not deserialize config field '" + configEntry.getName() + "' from file '" + configWrapper.getDestination().getFileName() + "' because the deserialized type '" + newValue.getClass().getSimpleName() + "' does not match the expected type '" + configEntry.getType().getSimpleName() + "'. That usually happens when you make a typo in your configuration file, so please check out that config field and correct any mistakes.";
                    logger.warning(msg);
                }
            });

        return configWrapper;
    }

    /**
     * Read all fields from the config file, and return a map with them.
     *
     * @param configWrapper the config that should have their file read
     * @return a map holding all "file names" fields mapped to their "file value"
     */
    abstract Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper);

    /**
     * Transform a map of "file names" to "file values" into a map of "java names" to "java values".
     * The keys are transformed simply by getting the java equivalent from {@code configWrapper#getNameMap()},
     * and values are transformed by serializing the file values and deserializing them using the
     * {@link Field#getGenericType()}.
     *
     * @param configWrapper the config wrapper related to the {@code fileValues} map
     * @param fileValues the simple map read from the file, contains only java native serializable types
     * @return a map of "java names" to "java values" adapted to the config field types
     */
    private Map<String, Object> mapFileValuesToJavaValues(final ConfigWrapper<?> configWrapper, final Map<String, Object> fileValues) {
        final Gson gson = gsonFactory.getInstance();
        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();

        return configEntries.stream().sequential()
            .map(configEntry -> {
                final String fileName = configEntry.getNameOnFile();
                final Object fileValue = fileValues.get(fileName);
                if (fileValue == null) return null;

                final Object javaValue;
                try {
                    javaValue = gson.fromJson(gson.toJson(fileValue), configEntry.getGenericType());
                } catch (final JsonParseException e) {
                    throw new ConfigSerializationException("sc-cfg doesn't know how to serialize field " + configEntry.getName() + " in config class '" + configEntry.getOwnerClass().getName() + "', consider adding a Type Adapter for this field type", e);
                }
                return new Pair<>(configEntry.getName(), javaValue);
            })
            .filter(Objects::nonNull)
            .collect(Maps.toMap());
    }

    @Override
    public void saveConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        createFileIfMissing(config, path);
        saveCurrentInstanceValues(configWrapper);
    }

    @Override
    public boolean saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        if(!createFileIfMissing(config, path)) return false;
        saveToFile(configWrapper, configWrapper.getDefaults());
        return true;
    }

    private void saveCurrentInstanceValues(final ConfigWrapper<?> configWrapper) {
        final Object instance = configWrapper.getInstance();
        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();
        saveToFile(configWrapper, getCurrentValues(instance, configEntries));
    }

    abstract void saveToFile(final ConfigWrapper<?> configWrapper, final Map<String, Object> newValues);

    protected boolean createFileIfMissing(final Object configInstance, final Path path) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(path, "path");

        if (Files.exists(path)) return false;
        try {
            final Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An error has occurred when creating config file for class " + configInstance.getClass().getName() + ".", e);
            throw new ConfigException(e);
        }
        return true;
    }

    @Override
    public Map<String, Object> getCurrentValues(final Object configInstance, final Set<ConfigEntry> configEntries) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(configEntries, "configEntries");

        final Gson gson = gsonFactory.getInstance();
        final Map<String, Object> currentValues = new LinkedHashMap<>(configEntries.size());

        configEntries.stream().sequential()
            .forEach(configEntry -> {
                final String nameOnFile = configEntry.getNameOnFile();

                try {
                    final Type fieldType = configEntry.getGenericType();
                    final Object copyValue = gson.fromJson(gson.toJson(configEntry.get(), fieldType), fieldType);
                    currentValues.put(nameOnFile, copyValue);
                } catch (final RuntimeException e) {
                    throw new ConfigSerializationException("sc-cfg doesn't know how to serialize field '" + configEntry.getName() + "' in config class '" + configInstance.getClass().getName() + "', consider adding a Type Adapter for " + configEntry.getGenericType() + ".", e);
                }
            });

        return Maps.immutableOf(currentValues);
    }

    protected void setValueOnField(final ConfigEntry configEntry, Object value) throws IllegalArgumentException, JsonSyntaxException {
        checkNotNull(configEntry, "configEntry");
        checkNotNull(value, "value");

        final Class<?> requiredType = configEntry.getType();
        final Class<?> providedType = value.getClass();

        if (!requiredType.equals(providedType) && !requiredType.isAssignableFrom(providedType)) {
            final Gson gson = gsonFactory.getInstance();
            value = gson.fromJson(gson.toJson(value), configEntry.getGenericType());
            if (value == null) {
                logger.warning("Oops, seems like Gson conversion of file value to java value returned null for field " + configEntry.getName() + " (from class " + configEntry.getOwnerClass().getName() + "), skipping value set on this config entry.");
                return;
            }
        }
        configEntry.set(value);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static final Type linkedMapToken = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
}
