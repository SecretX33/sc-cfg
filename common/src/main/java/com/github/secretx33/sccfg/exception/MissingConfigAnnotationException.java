package com.github.secretx33.sccfg.exception;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class MissingConfigAnnotationException extends ConfigException {

    public MissingConfigAnnotationException(final Class<?> clazz) {
        super("Could not create instance of class " + checkNotNull(clazz, "clazz cannot be null").getName() + " because it is missing @Configuration annotation, please annotate your configuration class with @Configuration");
    }
}
