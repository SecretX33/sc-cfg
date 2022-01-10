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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Maps {

    private Maps() {}

    /**
     * Returns an unmodifiable view of the {@code map}.
     *
     * @param map map to be wrapped by an immutable view
     * @param <K> the key type of the map
     * @param <V> the value type of the map
     * @return an unmodifiable view of the {@code map}.
     */
    public static <K, V> Map<K, V> of(@Nullable final Map<K, V> map) {
        if (map == null || map.isEmpty()) return Collections.emptyMap();
        if ("java.util.Collections$UnmodifiableMap".equals(map.getClass().getName())) {
            return map;
        }
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> copyPutting(final Map<? extends K, ? extends V> map, final K key, final V value) {
        checkNotNull(map, "map");
        checkNotNull(key, "key");
        checkNotNull(value, "value");
        return copyApplying(map, m -> m.put(key, value));
    }

    public static <K, V> Map<K, V> copyPutting(final Map<? extends K, ? extends V> map, final Map<? extends K, ? extends V> otherMap) {
        checkNotNull(map, "map");
        checkNotNull(otherMap, "otherMap");
        return copyApplying(map, m -> m.putAll(otherMap));
    }

    public static <K, V> Map<K, V> copyApplying(final Map<? extends K, ? extends V> map, final Consumer<Map<K, V>> consumer) {
        checkNotNull(map, "map");
        checkNotNull(consumer, "consumer");

        final Map<K, V> newMap = new LinkedHashMap<>(map);
        consumer.accept(newMap);
        return Collections.unmodifiableMap(newMap);
    }

    private static <K, V> Collector<Pair<? extends K, ? extends V>, ?, LinkedHashMap<K, V>> toMutableMap() {
        return Collectors.toMap(Pair::getFirst,
                Pair::getSecond,
                (a, b) -> b,
                LinkedHashMap::new);
    }

    public static <K, V> Collector<Pair<? extends K, ? extends V>, ?, Map<K, V>> toMap() {
        return Collectors.collectingAndThen(toMutableMap(), Collections::unmodifiableMap);
    }
}
