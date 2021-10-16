package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.factory.GsonFactory;

import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class JsonSerializer extends AbstractSerializer {

    public JsonSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
    }

    @Override
    public void saveConfig(final ConfigWrapper<?> configWrapper) {
        return;
    }

    @Override
    public void saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
    }
}
