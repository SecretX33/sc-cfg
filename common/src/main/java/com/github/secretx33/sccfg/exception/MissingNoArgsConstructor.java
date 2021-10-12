package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class MissingNoArgsConstructor extends ConfigException {

    public MissingNoArgsConstructor(final Class<?> clazz) {
        super("Could not create instance of class " + checkNotNull(clazz, "clazz cannot be null").getName() + " because it is missing no args constructor, please add an empty arg constructor to the class");
    }
}
