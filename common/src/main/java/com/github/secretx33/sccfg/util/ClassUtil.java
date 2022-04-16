package com.github.secretx33.sccfg.util;

import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ClassUtil {

    private ClassUtil() {}

    public static <T> Constructor<T> zeroArgsConstructor(final Class<T> clazz) {
        Constructor<T> constructor = zeroArgsConstructorOrNull(clazz);
        if (constructor == null) {
            throw new MissingNoArgsConstructorException(clazz);
        }
        return constructor;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> zeroArgsConstructorOrNull(final Class<T> clazz) {
        checkNotNull(clazz, "clazz");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                constructor.setAccessible(true);
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    public static boolean hasZeroArgsConstructor(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }
}
