package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class ConfigDeserializationException extends ConfigException {

    public ConfigDeserializationException(final Throwable cause) {
        super(checkNotNull(cause, "cause"));
    }
}
