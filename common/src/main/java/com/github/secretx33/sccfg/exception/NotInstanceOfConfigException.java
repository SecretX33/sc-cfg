package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class NotInstanceOfConfigException extends ConfigException {

    public NotInstanceOfConfigException(final Class<?> clazz) {
        super("Instance of class " + checkNotNull(clazz, "clazz").getName() + " passed as argument is not an instance of an initialized config.");
    }
}
