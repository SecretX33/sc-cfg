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
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
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
        final Map<String, Object> fileValues = loadFromFile(configWrapper);

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
     * @param configWrapper the config that should have its file read
     * @return a map holding all "java names" fields mapped to their "file value"
     */
    abstract Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper);

    /**
     * Save all values from the config into its file.
     *
     * @param configWrapper the config that should have their values saved
     * @param newValues the new values that should replace the current ones on the file
     * @throws ConfigSerializationException if sc-cfg could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigException when the file could not be saved because of some disk error
     */
    abstract void saveToFile(final ConfigWrapper<?> configWrapper, final Map<String, Object> newValues);

    @Override
    public boolean saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        if (!createFileIfMissing(config, path)) return false;
        saveToFile(configWrapper, configWrapper.getDefaults());
        return true;
    }

    protected boolean createFileIfMissing(final Object configInstance, final Path path) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(path, "path");

        if (Files.exists(path)) {
            if (!Files.isRegularFile(path)) {
                throw new ConfigException("File '" + path.getFileName() + "' was expected to be a file, but it's not (it's probably a folder).");
            }
            return false;
        }

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
    public void saveConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        createFileIfMissing(config, path);
        saveCurrentInstanceValues(configWrapper);
    }

    private void saveCurrentInstanceValues(final ConfigWrapper<?> configWrapper) {
        final Object instance = configWrapper.getInstance();
        final Set<ConfigEntry> configEntries = configWrapper.getConfigEntries();
        saveToFile(configWrapper, getCurrentValues(instance, configEntries));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getCurrentValues(final Object configInstance, final Set<ConfigEntry> configEntries) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(configEntries, "configEntries");

        final Gson gson = gsonFactory.getInstance();
        final Map<String, Object> currentValues = new LinkedHashMap<>();

        configEntries.forEach(configEntry -> {
            final String nameOnFile = configEntry.getNameOnFile();
            final Type fieldType;
            final Object copyValue;

            try {
                fieldType = configEntry.getGenericType();
                copyValue = gson.fromJson(gson.toJson(configEntry.get(), fieldType), fieldType);
            } catch (final RuntimeException e) {
                throw new ConfigSerializationException("sc-cfg doesn't know how to serialize field '" + configEntry.getName() + "' in config class '" + configInstance.getClass().getName() + "', consider adding a Type Adapter for " + configEntry.getGenericType() + ".", e);
            }

            if (configEntry.isAtRoot()) {
                currentValues.put(nameOnFile, copyValue);
            } else {
                final String[] composedPath = configEntry.getPath().split("\\.");
                Map<Object, Object> currentLayer = null;

                for(int i = 0; i < composedPath.length; i++) {
                    final Object object;
                    if (i == 0) {
                        object = currentValues.computeIfAbsent(composedPath[i], n -> new LinkedHashMap<>());
                    } else {
                        object = currentLayer.computeIfAbsent(composedPath[i], n -> new LinkedHashMap<>());
                    }

                    if (object instanceof Map<?, ?>) {
                        currentLayer = (Map<Object, Object>)object;
                    } else {
                        currentLayer = new LinkedHashMap<>();
                    }
                }

                if (currentLayer != null) {
                    currentLayer.put(nameOnFile, copyValue);
                }
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
