package com.github.secretx33.sccfg.api.annotation;

import com.github.secretx33.sccfg.api.Naming;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to set the name of the property (overrides any selected {@link Naming}).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    /**
     * Set the passed value as name of this config property. Cannot be blank, otherwise will throw
     * {@code IllegalArgumentException}.
     *
     * @return the name set of this config property
     */
    String value();
}
