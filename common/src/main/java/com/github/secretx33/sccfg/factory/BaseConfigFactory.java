package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructor;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.storage.FileModificationType;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BaseConfigFactory implements ConfigFactory {

    private final Map<Class<?>, ConfigWrapper<?>> instances = new ConcurrentHashMap<>();
    private final Path basePath;
    private final Scanner scanner;
    private final FileWatcher fileWatcher;

    public BaseConfigFactory(final Path basePath, final Scanner scanner, final FileWatcher fileWatcher) {
        this.basePath = checkNotNull(basePath);
        this.scanner = checkNotNull(scanner);
        this.fileWatcher = checkNotNull(fileWatcher);
    }

    @Override
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
            final Path destination = basePath.resolve(parseConfigPath(clazz, annotation));
            final Set<MethodWrapper> runBeforeReload = scanner.getBeforeReloadMethods(clazz);
            final Set<MethodWrapper> runAfterReload = scanner.getAfterReloadMethods(clazz);

            final ConfigWrapper<T> wrapper = new ConfigWrapper<>(instance, annotation, destination, runBeforeReload, runAfterReload);
            final FileWatcher.WatchedLocation watchedLocation = fileWatcher.getWatcher(destination);
            watchedLocation.addListener(FileModificationType.CREATE_AND_MODIFICATION, handleReload(wrapper));
            return wrapper;
        } catch (Exception e) {
            throw new ConfigException(e);
        }
    }

    private String parseConfigPath(final Class<?> clazz, final Configuration configuration) {
        final String value = configuration.value().trim();
        final String extension = configuration.type().extension;
        if (value.isEmpty()) {
            return clazz.getName() + extension;
        }
        if(value.toLowerCase(Locale.US).endsWith(extension)) {
            return value;
        }
        return value + extension;
    }

    private Configuration getConfigAnnotation(final Class<?> clazz) {
        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if(annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }
        return annotation;
    }

    protected Consumer<FileWatcherEvent> handleReload(final ConfigWrapper<?> configWrapper) {
        return event -> {
            // TODO handle config reload
            System.out.println("Config reloaded...");
        };
    }
}
