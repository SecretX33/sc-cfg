package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;

import java.util.Map;

public interface Serializer {

    /**
     * Load the config from the disk, saving defaults if file is not present, and apply those
     * read configs to the config {@link ConfigWrapper#getInstance() instance}.
     *
     * @param configWrapper the wrapped config
     * @param <T> the inner type of that ConfigWrapper
     * @return the ConfigWrapper passed as argument, for convenience
     * @throws ConfigException if a field from the config was not accessible, but this exception
     * should not happen unless some java modification breaks something related to field access.
     */
    <T> ConfigWrapper<T> loadConfig(ConfigWrapper<T> configWrapper);

    /**
     * Save current config values to the disk.
     *
     * @param configWrapper the wrapped config to be saved to the disk
     * @throws ConfigSerializationException if serializer could not parse some field
     * @throws ConfigException if an error occurs while saving the config to the disk
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
    boolean saveDefault(ConfigWrapper<?> configWrapper);

    /**
     * Extract the current values from a config instance, applying the specified {@code nameMap}
     * mapping to the variable names.
     *
     * @param configInstance the config to extract default values from
     * @param nameMap the mapping from the java names to the file names
     * @return the default values for that config instance
     * @throws ConfigSerializationException if serializer could not parse some field
     */
    Map<String, Object> getCurrentValues(Object configInstance, NameMap nameMap);
}
