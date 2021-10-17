package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigOverrideException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;
import com.github.secretx33.sccfg.exception.NotInstanceOfConfigException;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.Serializer;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;
import com.github.secretx33.sccfg.storage.FileModificationType;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import com.github.secretx33.sccfg.util.Valid;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BaseConfigFactory implements ConfigFactory {

    private final Map<Class<?>, ConfigWrapper<?>> instances = new ConcurrentHashMap<>();
    private final Path basePath;
    private final Scanner scanner;
    private final FileWatcher fileWatcher;
    private final SerializerFactory serializerFactory;

    public BaseConfigFactory(final Path basePath, final Scanner scanner, final FileWatcher fileWatcher,  final SerializerFactory serializerFactory) {
        this.basePath = checkNotNull(basePath, "basePath");
        this.scanner = checkNotNull(scanner, "scanner");
        this.fileWatcher = checkNotNull(fileWatcher, "fileWatcher");
        this.serializerFactory = checkNotNull(serializerFactory, "serializerFactory");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ConfigWrapper<T> getWrapper(final Class<T> clazz) {
        checkNotNull(clazz, "clazz");
        return (ConfigWrapper<T>) instances.computeIfAbsent(clazz, this::newWrappedConfigInstance);
    }

    private <T> ConfigWrapper<T> newWrappedConfigInstance(final Class<T> clazz) {
        Valid.validateConfigClass(clazz);
        final Constructor<T> constructor = getDefaultConstructor(clazz);
        try {
            final T instance = constructor.newInstance();
            return wrapInstance(instance);
        } catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
            throw new ConfigException(e);
        }
    }

    private <T> ConfigWrapper<T> wrapInstance(final T instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot register classes as instances of configuration");

        final Class<?> clazz = instance.getClass();
        final Configuration annotation = getConfigAnnotation(clazz);
        final Serializer serializer = serializerFactory.getFor(annotation.type());
        final Set<Field> configFields = scanner.getConfigurationFields(clazz);
        final NameMap nameMap = serializerFactory.getNameMapper().mapFieldNamesUsing(configFields, annotation.nameStrategy());
        final Map<String, Object> defaults = serializer.getDefaults(instance, nameMap);
        try {
            final Path configPath = Paths.get(parseConfigPath(clazz, annotation));
            final Path destination = basePath.resolve(configPath);
            final Set<MethodWrapper> runBeforeReload = scanner.getBeforeReloadMethods(clazz);
            final Set<MethodWrapper> runAfterReload = scanner.getAfterReloadMethods(clazz);

            final ConfigWrapper<T> wrapper = new ConfigWrapper<>(instance, annotation, destination, defaults, nameMap, configFields, runBeforeReload, runAfterReload);
            final FileWatcher.WatchedLocation watchedLocation = fileWatcher.getWatcher(configPath);
            watchedLocation.addListener(FileModificationType.CREATE_AND_MODIFICATION, handleReload(wrapper));
            watchedLocation.recordChange(destination);
            return serializer.loadConfig(wrapper);
        }  catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
            throw new ConfigException(e);
        }
    }

    private String parseConfigPath(final Class<?> clazz, final Configuration configuration) {
        final String value = configuration.value().trim();
        final String lowerCasedValue = value.toLowerCase(Locale.US);
        final String extension = configuration.type().extension;
        if (value.isEmpty()) {
            return clazz.getName() + extension;
        }
        if(lowerCasedValue.endsWith(extension)) {
            return value.substring(0, lowerCasedValue.lastIndexOf(extension) + 1) + extension;
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

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny()
                .map(c -> (Constructor<T>)c)
                .orElseThrow(() -> new MissingNoArgsConstructorException(clazz));
    }

    @Override
    public void registerInstance(final Object instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot register classes as instances of configuration");

        final Class<?> clazz = instance.getClass();
        Valid.validateConfigClass(clazz);

        if(instances.containsKey(clazz)) {
            throw new ConfigOverrideException(clazz);
        }
        final ConfigWrapper<?> wrapper = wrapInstance(instance);
        instances.put(clazz, wrapper);
    }

    protected Consumer<FileWatcherEvent> handleReload(final ConfigWrapper<?> configWrapper) {
        return event -> {
            // TODO handle config reload
            System.out.println("Config reloaded...");
        };
    }

    @Override
    public void saveInstance(final Object instance) {
        checkNotNull(instance, "instance");

        final Class<?> clazz = instance.getClass();
        Valid.validateConfigClass(clazz);
        ConfigWrapper<?> wrapper = instances.get(clazz);
        if (wrapper == null) {
            throw new NotInstanceOfConfigException(clazz);
        }
        final Configuration annotation = getConfigAnnotation(clazz);
        serializerFactory.getFor(annotation.type()).saveConfig(wrapper);
    }

    protected void reloadInstance(final ConfigWrapper<?> configWrapper) {
        serializerFactory.getFor(configWrapper.getFileType()).loadConfig(configWrapper);
    }
}
