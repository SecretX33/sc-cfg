package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class ConfigSerializationException extends ConfigException {

    public ConfigSerializationException(final Throwable cause) {
        super(checkNotNull(cause, "cause"));
    }

    public ConfigSerializationException(final String message, final Throwable cause) {
        super(checkNotNull(message, "message"), checkNotNull(cause, "cause"));
    }
}
