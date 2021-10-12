package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructor;
import com.github.secretx33.sccfg.scanner.Scanner;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ConfigFactory {

    private final Map<Class<?>, ConfigWrapper<?>> instances = new ConcurrentHashMap<>();
    private final Scanner scanner;

    public ConfigFactory(Scanner scanner) {
        this.scanner = checkNotNull(scanner);
    }

    @SuppressWarnings("unchecked")
    public <T> ConfigWrapper<T> getWrapper(final Class<T> clazz) {
        checkNotNull(clazz, "clazz cannot be null");
        return (ConfigWrapper<T>) instances.computeIfAbsent(clazz, this::newWrappedConfigInstance);
    }

    @SuppressWarnings("unchecked")
    private <T> ConfigWrapper<T> newWrappedConfigInstance(final Class<T> clazz) {
        final Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
            .filter(c -> c.getParameterCount() == 0)
            .findAny()
            .orElseThrow(() -> new MissingNoArgsConstructor(clazz));
        try {
            constructor.setAccessible(true);

            final T instance = (T) constructor.newInstance();
            final Configuration annotation = getConfigAnnotation(clazz);
            final Set<Method> runBeforeReload = scanner.getBeforeReloadMethods(clazz);
            final Set<Method> runAfterReload = scanner.getAfterReloadMethods(clazz);

            return new ConfigWrapper<>(instance, annotation);
        } catch (ReflectiveOperationException e) {
            throw new ConfigException(e);
        }
    }

    private Configuration getConfigAnnotation(final Class<?> clazz) {
        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if(annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }
        return annotation;
    }
}
