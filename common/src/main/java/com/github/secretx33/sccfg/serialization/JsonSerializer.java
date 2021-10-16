package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class JsonSerializer implements Serializer {

    private final Logger logger;

    public JsonSerializer(final Logger logger) {
        this.logger = checkNotNull(logger, "logger");
    }

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
    }

    @Override
    public boolean saveConfig(final ConfigWrapper<?> configWrapper) {
        return true;
    }

    @Override
    public void saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
    }
}
