package com.github.secretx33.sccfg.exception;

public final class ConfigOverrideException extends ConfigException {

    public ConfigOverrideException(final Class<?> clazz) {
        super("There's already an instance of config " + clazz.getName() + " registered, you cannot override config instances");
    }
}
