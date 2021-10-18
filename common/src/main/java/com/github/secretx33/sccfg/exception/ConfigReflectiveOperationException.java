package com.github.secretx33.sccfg.exception;

public final class ConfigReflectiveOperationException extends ConfigException {

    public ConfigReflectiveOperationException(final String message) {
        super(message);
    }

    public ConfigReflectiveOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigReflectiveOperationException(final Throwable cause) {
        super(cause);
    }
}
