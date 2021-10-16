package com.github.secretx33.sccfg.util;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructor;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Valid {

    private Valid() {}

    public static boolean isConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            return false;
        }

        final boolean hasDefaultConstructor = Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0);
        if (!hasDefaultConstructor) {
            return false;
        }
        return true;
    }

    public static void validateConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }

        final boolean hasDefaultConstructor = Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0);
        if (!hasDefaultConstructor) {
            throw new MissingNoArgsConstructor(clazz);
        }
    }

    public static void validateConfigClasses(final Class<?>... classes) {
        checkNotNull(classes, "classes");
        Arrays.stream(classes).forEach(Valid::validateConfigClass);
    }

    public static void validateConfigClasses(final Object... instances) {
        checkNotNull(instances, "instances");
        Arrays.stream(instances).map(Object::getClass).forEach(Valid::validateConfigClass);
    }

    private <T> Constructor<?> getDefaultConstructor(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny()
                .orElseThrow(() -> new MissingNoArgsConstructor(clazz));
    }
}
