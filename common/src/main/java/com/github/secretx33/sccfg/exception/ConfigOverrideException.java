package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ConfigOverrideException extends ConfigException {

    public ConfigOverrideException(final Class<?> clazz) {
        super("There's already an instance of config " + checkNotNull(clazz, "clazz").getName() + " registered, you cannot override config instances");
    }
}
