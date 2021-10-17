package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class MissingNoArgsConstructorException extends ConfigException {

    public MissingNoArgsConstructorException(final Class<?> clazz) {
        super("Could not create instance of class " + checkNotNull(clazz, "clazz").getName() + " because it is missing no args constructor, please add an empty arg constructor to the class");
    }
}