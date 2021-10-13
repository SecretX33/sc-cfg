package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ConfigOverrideException extends ConfigException {

    public ConfigOverrideException(final Class<?> clazz) {
        super("You cannot override an instance of a config, so I could not re-register your instance of " + checkNotNull(clazz, "clazz cannot be null").getName());
    }
}
