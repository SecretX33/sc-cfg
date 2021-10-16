package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public final class Preconditions {

    public static void checkArgument(final boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException();
        }
    }

    public static void checkArgument(boolean expression, final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkArgument(boolean expression, final Supplier<String> errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    public static <T> T checkNotNull(@Nullable final T reference) {
        if (reference == null) {
            throw new NullPointerException("value cannot be null");
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable final T reference, final String errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage);
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable final T reference, final Supplier<String> errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage.get());
        }
        return reference;
    }

    public static <K, V> Map<K, V> notContainsNull(@Nullable final Map<K, V> reference) {
        if (reference == null) {
            throw new NullPointerException("map cannot be null");
        }
        reference.forEach((key, value) -> {
            if (key == null) {
                throw new NullPointerException("map passed as argument cannot hold null keys");
            }
            if (value == null) {
                throw new NullPointerException("map passed as argument cannot hold null values");
            }
        });
        return reference;
    }
}
