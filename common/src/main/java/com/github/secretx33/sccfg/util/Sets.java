package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Sets {

    private Sets() {}

    @SafeVarargs
    public static <T, S extends T> Set<T> of(final S... elements) {
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
    public static <T> T[] toArray(final Class<T> clazz, @Nullable final Set<? extends T> set) {
        if(set == null || set.isEmpty())
            return (T[]) Array.newInstance(clazz, 0);

        final T[] array = (T[]) Array.newInstance(clazz, set.size());
        return set.toArray(array);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Class<T> clazz, final Set<T>... sets) {
        if(sets.length == 0)
            return (T[]) Array.newInstance(clazz, 0);

        return Arrays.stream(sets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toArray(size -> (T[]) Array.newInstance(clazz, size));
    }

    public static <T> Collector<T, ?, LinkedHashSet<T>> toLinkedSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    public static <T> Collector<T, ?, Set<T>> toImmutableLinkedSet() {
        return Collectors.collectingAndThen(toLinkedSet(), Collections::unmodifiableSet);
    }
}
