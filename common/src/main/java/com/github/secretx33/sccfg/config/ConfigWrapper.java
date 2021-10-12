package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Set;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class ConfigWrapper<T> {

    private final T instance;
    private final Configuration configAnnotation;
    private final
    private final Set<Method> runBeforeReload;
    private final Set<Method> runAfterReload;

    public ConfigWrapper(T instance, Configuration configAnnotation) {
        this.instance = checkNotNull(instance);
        this.configAnnotation = checkNotNull(configAnnotation);
    }

    public T getInstance() {
        return instance;
    }

    public Configuration getConfigAnnotation() {
        return configAnnotation;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigWrapper<?> that = (ConfigWrapper<?>) o;
        return instance.equals(that.instance) && configAnnotation.equals(that.configAnnotation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, configAnnotation);
    }

    @Override
    public String toString() {
        return "ConfigWrapper{" +
                "instance=" + instance +
                ", configAnnotation=" + configAnnotation +
                '}';
    }
}
