package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class JsonSerializer implements Serializer {

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper cannot be null");
    }

    @Override
    public void saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper cannot be null");
    }
}
