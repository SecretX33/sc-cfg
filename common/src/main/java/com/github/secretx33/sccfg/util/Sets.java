/*
 * Copyright (C) 2021 SecretX <notyetmidnight@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Sets {

    private Sets() {}

    @SafeVarargs
    public static <T> Set<T> mutableOf(final T... elements) {
        return Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Sets.toMutableSet());
    }

    @SafeVarargs
    public static <T extends Enum<T>> Set<T> mutableOf(final T... elements) {
        if (elements.length == 0) return new HashSet<>();
        return EnumSet.copyOf(Arrays.stream(elements)
                .filter(Objects::nonNull)
                .collect(Sets.toMutableSet()));
    }

    @SafeVarargs
    public static <T> Set<T> of(final T... elements) {
        if (elements.length == 0) return Collections.emptySet();
        return Collections.unmodifiableSet(mutableOf(elements));
    }

    @SafeVarargs
    public static <T extends Enum<T>> Set<T> of(final T... elements) {
        if (elements.length == 0) return Collections.emptySet();
        return Collections.unmodifiableSet(mutableOf(elements));
    }

    public static <T extends Enum<T>> Set<T> copyOf(final Set<T> set) {
        if (set.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(EnumSet.copyOf(set));
    }

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Class<T> clazz, @Nullable final Set<? extends T> set) {
        if (set == null || set.isEmpty())
            return (T[]) Array.newInstance(clazz, 0);

        final T[] array = (T[]) Array.newInstance(clazz, set.size());
        return set.toArray(array);
    }

    @SafeVarargs
    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(final Class<T> clazz, final Set<T>... sets) {
        if (sets.length == 0)
            return (T[]) Array.newInstance(clazz, 0);

        return Arrays.stream(sets)
                .filter(Objects::nonNull)
                .flatMap(Collection::stream)
                .toArray(size -> (T[]) Array.newInstance(clazz, size));
    }

    public static <T> Set<T> filter(final Iterable<? extends T> iterable, final Predicate<T> filter) {
        final Set<T> set = new LinkedHashSet<>();
        for (final T element : iterable) {
            if (filter.test(element)) {
                set.add(element);
            }
        }
        if (set.isEmpty()) return Collections.emptySet();
        return Collections.unmodifiableSet(set);
    }

    private static <T> Collector<T, ?, LinkedHashSet<T>> toMutableSet() {
        return Collectors.toCollection(LinkedHashSet::new);
    }

    public static <T> Collector<T, ?, Set<T>> toSet() {
        return Collectors.collectingAndThen(toMutableSet(), Collections::unmodifiableSet);
    }
}
