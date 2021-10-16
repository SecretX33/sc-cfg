package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class Maps {

    public static <K, V> Map<K, V> immutableOf(@Nullable final Map<K, V> map) {
        if (map == null || map.isEmpty()) return Collections.emptyMap();
        return Collections.unmodifiableMap(map);
    }

    public static <K, V> Map<K, V> immutableCopyPutting(final Map<K, V> map, final K key, final V value) {
        checkNotNull(map, "map cannot be null");
        checkNotNull(key, "key cannot be null");
        checkNotNull(value, "value cannot be null");
        return immutableCopyApplying(map, m -> m.put(key, value));
    }

    public static <K, V> Map<K, V> immutableCopyPutting(final Map<K, V> map, final Map<K, V> otherMap) {
        checkNotNull(map, "map cannot be null");
        checkNotNull(otherMap, "otherMap cannot be null");
        return immutableCopyApplying(map, m -> m.putAll(otherMap));
    }

    public static <K, V> Map<K, V> immutableCopyApplying(final Map<K, V> map, final Consumer<Map<K, V>> consumer) {
        checkNotNull(map, "map cannot be null");
        checkNotNull(consumer, "consumer cannot be null");

        final Map<K, V> newMap = new HashMap<>(map);
        consumer.accept(newMap);
        return Collections.unmodifiableMap(newMap);
    }
}
