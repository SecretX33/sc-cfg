package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class Sets {

    private Sets() {}

    @SafeVarargs
    public static <T> Set<T> of(final T... elements) {
        return Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    @SafeVarargs
    public static <T> Set<T> immutableOf(final T... elements) {
        return Collections.unmodifiableSet(of(elements));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(@Nullable final Set<T> set) {
        if(set == null) return (T[]) EMPTY_ARRAY;
        return (T[]) set.toArray();
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Set<T>... sets) {
        return (T[]) Arrays.stream(sets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toArray();
    }

    private static final Object[] EMPTY_ARRAY = new Object[0];
}
