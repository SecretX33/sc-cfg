/*
 * Copyright (C) 2021-2022 SecretX <notyetmidnight@gmail.com>
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

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;

public final class ExpiringMap<K, V> {

    private final Cache<K, V> cache;

    public ExpiringMap(final long duration, final TimeUnit unit) {
        checkArgument(duration > 0L, "duration has to be greater than zero");
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit).build();
    }

    public boolean contains(final K key, final V value) {
        final V currentValue = cache.getIfPresent(key);
        return value.equals(currentValue);
    }

    public boolean containsKey(@Nullable final K key) {
        if (key == null) return false;
        return cache.getIfPresent(key) != null;
    }

    public boolean containsValue(@Nullable final V value) {
        if (value == null) return false;
        return cache.asMap().containsValue(value);
    }

    @Nullable
    public V get(final K key) {
        return cache.getIfPresent(key);
    }

    /**
     * Add the key and value to the map, returning if it was not present before.
     *
     * @param key the key to be added to the map
     * @param value the value to be added to the map
     * @return <code>true</code> if the key with that value was not present before
     */
    public boolean put(final K key, final V value) {
        final boolean contains = contains(key, value);
        cache.put(key, value);
        return !contains;
    }

    @Nullable
    public V remove(K key) {
        final V previous = cache.getIfPresent(key);
        cache.invalidate(key);
        return previous;
    }

    public void putAll(final Map<? extends K, ? extends V> map) {
        cache.putAll(map);
    }

    public int size() {
        cache.cleanUp();
        return (int) cache.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public void clear() {
        cache.invalidateAll();
    }

    @NotNull
    public Set<Map.Entry<K, V>> entrySet() {
        cache.cleanUp();
        return cache.asMap().entrySet();
    }

    @NotNull
    public Set<K> keySet() {
        cache.cleanUp();
        return cache.asMap().keySet();
    }

    @NotNull
    public Collection<V> values() {
        cache.cleanUp();
        return cache.asMap().values();
    }
}
