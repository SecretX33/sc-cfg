package com.github.secretx33.sccfg.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;

public class ExpiringSet<T> {

    private final Cache<T, Long> cache;
    private final long lifetime;

    public ExpiringSet(long duration, TimeUnit unit) {
        checkArgument(duration > 0L, "duration has to be greater than zero");
        this.lifetime = unit.toMillis(duration);
        this.cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit).build();
    }

    public int size() {
        return (int) cache.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public boolean contains(@Nullable T element) {
        if (element == null) return false;
        final Long timeout = cache.getIfPresent(element);
        return timeout != null && timeout > System.currentTimeMillis();
    }

    @NotNull
    public Iterator<T> iterator() {
        return cache.asMap().keySet().iterator();
    }

    public boolean add(T element) {
        boolean present = contains(element);
        cache.put(element, System.currentTimeMillis() + lifetime);
        return !present;
    }

    public boolean remove(T element) {
        boolean present = contains(element);
        cache.invalidate(element);
        return present;
    }

    public boolean containsAll(@NotNull Collection<T> c) {
        return cache.asMap().keySet().containsAll(c);
    }

    public boolean addAll(@NotNull Collection<? extends T> c) {
        return cache.asMap().keySet().addAll(c);
    }

    public boolean retainAll(@NotNull Collection<T> c) {
        return cache.asMap().keySet().retainAll(c);
    }

    public boolean removeAll(@NotNull Collection<T> c) {
        return cache.asMap().keySet().removeAll(c);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpiringSet<?> that = (ExpiringSet<?>) o;
        return lifetime == that.lifetime && cache.equals(that.cache);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cache, lifetime);
    }

    @Override
    public String toString() {
        return "ExpiringSet{" +
                "cache=" + cache +
                ", lifetime=" + lifetime +
                '}';
    }
}
