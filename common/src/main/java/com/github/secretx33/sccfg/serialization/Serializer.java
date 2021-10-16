package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

public interface Serializer {

    void loadConfig(ConfigWrapper<?> configWrapper);
    void saveDefault(ConfigWrapper<?> configWrapper);
}
