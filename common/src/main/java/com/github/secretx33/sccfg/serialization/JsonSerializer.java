package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class JsonSerializer implements Serializer {

    private final Logger logger;

    public JsonSerializer(final Logger logger) {
        this.logger = checkNotNull(logger, "logger cannot be null");
    }

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper cannot be null");
    }

    @Override
    public void saveConfig(final ConfigWrapper<?> configWrapper) {

    }

    @Override
    public void saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper cannot be null");
    }
}
