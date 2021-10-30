package com.github.secretx33.sccfg.exception;

/**
 * Thrown when any kind of internal error happens inside the library. Represents some failure on our side,
 * and never on the consumer side.
 */
public class ConfigInternalErrorException extends ConfigException {

    public ConfigInternalErrorException(final String message) {
        super(message);
    }

    public ConfigInternalErrorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigInternalErrorException(final Throwable cause) {
        super(cause);
    }
}
