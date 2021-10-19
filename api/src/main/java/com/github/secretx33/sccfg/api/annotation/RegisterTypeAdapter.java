package com.github.secretx33.sccfg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Convenience annotation to allow quick registration of custom gson Type Adapters. Because of annotation
 * limitations, this can only be used on classes that doesn't suffer from type erasure (i.e. don't use
 * parameterized types), if you need to register a type adapter for a very specific type, you can use
 * {@code Config.registerTypeAdapter} method (and variations).
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterTypeAdapter {

    /**
     * The class that this TypeAdapter serialized and/or deserializes. <b>Requires overriding</b>,
     * if not overridden it'll throw {@code MissingTypeOverrideOnAdapterException}.
     */
    Class<?> value();
}
