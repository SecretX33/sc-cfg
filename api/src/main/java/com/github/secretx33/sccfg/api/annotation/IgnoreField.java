package com.github.secretx33.sccfg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can be used to make {@code sccfg} skip serialization of fields annotated with it
 * to the config file. Static and transient fields are always skipped.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreField {
}
