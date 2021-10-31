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
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;

import java.util.Map;
import java.util.Set;

/**
 * Represent classes responsible for loading and saving config files.
 */
public interface Serializer {

    /**
     * Load the config from the disk, saving defaults if file is not present, and apply those
     * read configs to the config {@link ConfigWrapper#getInstance() instance}.
     *
     * @param configWrapper the wrapped config
     * @param <T> the inner type of that ConfigWrapper
     * @return the ConfigWrapper passed as argument, for convenience
     * @throws ConfigDeserializationException if serializer could not deserialize a config entry
     * back to its java value (that happens when sc-cfg is missing a Type Adapter for that
     * specific type)
     * @throws ConfigReflectiveOperationException if a field from the config was not accessible, but this
     * exception should not happen unless some java modification breaks something related to field access
     */
    <T> ConfigWrapper<T> loadConfig(ConfigWrapper<T> configWrapper);

    /**
     * Save current config values to the disk.
     *
     * @param configWrapper the wrapped config to be saved to the disk
     * @throws ConfigSerializationException if serializer could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     * @throws ConfigReflectiveOperationException if a field from the config was not accessible, but this
     * exception should not happen unless some java modification breaks something related to field access
     */
    void saveConfig(ConfigWrapper<?> configWrapper);

    /**
     * Save the default values of that config to the disk, creating a new file if it doesn't already
     * exist. Does <b>not</b> overrides anything if the file already exists, <i>except</i> if the
     * file is empty.
     *
     * @param configWrapper the wrapper for the config instance
     * @return true if defaults were saved to the disk, false if the file already existed
     * @throws ConfigSerializationException if serializer could not parse some field
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    boolean saveDefaults(ConfigWrapper<?> configWrapper, boolean overrideIfExists);

    /**
     * Extract the current values from a config instance, applying the specified {@code nameMap}
     * mapping to the variable names.
     *
     * @param configInstance the config to extract default values from
     * @param configEntries set containing all the entries from {@code configInstance} that should have their value extracted
     * @return the default values for that config instance (the keys are <b>FILE</b> names, not java names)
     * @throws ConfigSerializationException if serializer could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     */
    Map<String, Object> getCurrentValues(Object configInstance, Set<ConfigEntry> configEntries);
}
