package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
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
    public static <T extends Enum<T>> Set<T> of(final T... elements) {
        if (elements.length == 0) return new HashSet<>();
        return EnumSet.copyOf(Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet()));
    }

    @SafeVarargs
    public static <T> Set<T> immutableOf(final T... elements) {
        if (elements.length == 0) return Collections.emptySet();
        return Collections.unmodifiableSet(of(elements));
    }

    @SafeVarargs
    public static <T extends Enum<T>> Set<T> immutableOf(final T... elements) {
        if (elements.length == 0) return Collections.emptySet();
        return Collections.unmodifiableSet(of(elements));
    }

    public static <T extends Enum<T>> Set<T> immutableCopyOf(final Set<T> set) {
        if (set.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(EnumSet.copyOf(set));
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
