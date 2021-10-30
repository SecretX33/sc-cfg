package com.github.secretx33.sccfg.exception;

/**
 * Thrown when any kind of internal error happens inside the library. Represents some failure on our side,
 * and never on the consumer side.
 */
public class ConfigInternalErrorException extends ConfigException {

    public ConfigInternalErrorException() {}

    public ConfigInternalErrorException(String message) {
        super(message);
    }

    public ConfigInternalErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigInternalErrorException(Throwable cause) {
        super(cause);
    }
}
