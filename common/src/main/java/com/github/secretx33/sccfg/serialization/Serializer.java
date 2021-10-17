package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

import java.util.Map;

public interface Serializer {

    <T> ConfigWrapper<T> loadConfig(ConfigWrapper<T> configWrapper);
    void saveConfig(ConfigWrapper<?> configWrapper);

    /**
     * Save the default values of that config to the disk, creating a new file if it doesn't already
     * exists. Does <b>not</b> overrides anything if the file already exists, <i>except</i> if the
     * file is empty.
     *
     * @param configWrapper the wrapper for the config instance
     * @return true if defaults were saved to the disk, false if the file already existed
     */
    boolean saveDefault(ConfigWrapper<?> configWrapper);
    Map<String, Object> getDefaults(Object configInstance);
}
