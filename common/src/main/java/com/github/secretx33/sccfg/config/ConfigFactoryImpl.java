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
package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.Naming;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.Name;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException;
import com.github.secretx33.sccfg.exception.ConfigInstanceOverrideException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;
import com.github.secretx33.sccfg.executor.AsyncExecutor;
import com.github.secretx33.sccfg.executor.AsyncMethodExecutor;
import com.github.secretx33.sccfg.executor.SyncExecutor;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.Serializer;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapperFactory;
import com.github.secretx33.sccfg.storage.FileModificationType;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import com.github.secretx33.sccfg.util.BooleanWrapper;
import com.github.secretx33.sccfg.util.Sets;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

public final class ConfigFactoryImpl implements ConfigFactory {

    private final Map<Class<?>, ConfigWrapper<?>> instances = new ConcurrentHashMap<>();
    private final Path basePath;
    private final Scanner scanner;
    private final FileWatcher fileWatcher;
    private final SerializerFactory serializerFactory;
    private final AsyncExecutor asyncExecutor;
    private final SyncExecutor syncExecutor;
    private final NameMapperFactory nameMapperFactory;

    public ConfigFactoryImpl(
            final Logger logger,
            final GsonFactory gsonFactory,
            final Path basePath,
            final Scanner scanner,
            final FileWatcher fileWatcher,
            final SyncExecutor syncExecutor
    ) {
        this.basePath = checkNotNull(basePath, "basePath");
        this.scanner = checkNotNull(scanner, "scanner");
        this.fileWatcher = checkNotNull(fileWatcher, "fileWatcher");
        this.serializerFactory = new SerializerFactory(logger, checkNotNull(gsonFactory, "gsonFactory"));
        this.asyncExecutor = new AsyncMethodExecutor(checkNotNull(logger, "logger"));
        this.syncExecutor = checkNotNull(syncExecutor, "syncExecutor");
        this.nameMapperFactory = new NameMapperFactory();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ConfigWrapper<T> getWrapper(final Class<T> configClass) {
        checkNotNull(configClass, "configClass");
        return (ConfigWrapper<T>) instances.computeIfAbsent(configClass, this::newWrappedConfigInstance);
    }

    private <T> ConfigWrapper<T> newWrappedConfigInstance(final Class<T> clazz) {
        Valid.validateConfigClassWithDefaultConstructor(clazz);
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
        final Serializer serializer = serializerFactory.getSerializer(annotation.type());
        final Set<Field> configFields = scanner.getConfigurationFields(clazz);
        final Set<ConfigEntry> configEntries = mapConfigEntries(instance, configFields, annotation.naming());
        final Map<String, Object> defaults = serializer.getCurrentValues(instance, configEntries);
        try {
            final Path configPath = Paths.get(parseConfigPath(clazz, annotation));
            final Path destination = basePath.resolve(configPath);
            final Set<MethodWrapper> runBeforeReload = scanner.getBeforeReloadMethods(clazz);
            final Set<MethodWrapper> runAfterReload = scanner.getAfterReloadMethods(clazz);

            final FileWatcher.WatchedLocation watchedLocation = fileWatcher.getWatcher(configPath);
            final ConfigWrapper<T> wrapper = new ConfigWrapperImpl<>(instance, annotation, destination, defaults, configEntries, runBeforeReload, runAfterReload, watchedLocation);
            watchedLocation.addListener(FileModificationType.CREATE_AND_MODIFICATION, handleReload(wrapper));
            return serializer.loadConfig(wrapper);
        } catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
            throw new ConfigException(e);
        }
    }

    private Configuration getConfigAnnotation(final Class<?> clazz) {
        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }
        return annotation;
    }

    private Set<ConfigEntry> mapConfigEntries(final Object instance, final Set<Field> fields, final Naming naming) {
        checkNotNull(instance, "instance");
        notContainsNull(fields, "fields");
        checkNotNull(naming, "naming");

        final NameMapper mapper = nameMapperFactory.getMapper(naming);

        return fields.stream().sequential().map(field -> {
            final com.github.secretx33.sccfg.api.annotation.Path pathAnnotation = field.getDeclaredAnnotation(com.github.secretx33.sccfg.api.annotation.Path.class);
            final String path;

            if (pathAnnotation == null) {
                path = "";
            } else {
                path = pathAnnotation.value();
                checkNotBlank(path, () -> "@Path annotation does not support null, empty or blank values, but you passed one of these three as parameter on your field " + field.getName() + " (which belongs to class " + field.getDeclaringClass().getSimpleName() + ")");
            }

            final Name nameAnnotation = field.getDeclaredAnnotation(Name.class);
            final String nameOnFile;

            if (nameAnnotation == null) {
                nameOnFile = mapper.applyStrategy(field.getName());
            } else {
                nameOnFile = nameAnnotation.value();
                checkNotBlank(nameOnFile, () -> "@Name annotation does not support null, empty or blank values, but you passed one of these three as value of @Name annotation on your field '" + field.getName() + "' (which belongs to class '" + field.getDeclaringClass().getSimpleName() + "')");
            }

            return new ConfigEntryImpl(instance, field, nameOnFile, path);
        }).collect(Sets.toSet());
    }

    private String parseConfigPath(final Class<?> clazz, final Configuration configuration) {
        String value = configuration.value().trim();
        if (value.isEmpty()) value = configuration.name().trim();
        final String lowerCasedValue = value.toLowerCase(Locale.US);
        final String extension = configuration.type().getExtension();
        if (value.isEmpty()) {
            return clazz.getName() + extension;
        }
        if (lowerCasedValue.endsWith(extension)) {
            return value.substring(0, lowerCasedValue.lastIndexOf(extension)) + extension;
        }
        return value + extension;
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny()
                .map(c -> (Constructor<T>) c)
                .orElseThrow(() -> new MissingNoArgsConstructorException(clazz));
    }

    private Consumer<FileWatcherEvent> handleReload(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
        return event -> handleReloadAsync(configWrapper);
    }

    private void handleReloadAsync(final ConfigWrapper<?> configWrapper) {
        asyncExecutor.delayedRun(200L, () -> {
            final Object instance = configWrapper.getInstance();
            final Set<MethodWrapper> asyncBefore = configWrapper.getRunBeforeReloadAsyncMethods();
            final Set<MethodWrapper> syncBefore = configWrapper.getRunBeforeReloadSyncMethods();
            final Set<MethodWrapper> syncAfter = configWrapper.getRunAfterReloadSyncMethods();
            final Set<MethodWrapper> asyncAfter = configWrapper.getRunAfterReloadAsyncMethods();
            final int runBeforeCount = asyncBefore.size() + syncBefore.size();
            final CountDownLatch latch = new CountDownLatch(runBeforeCount);

            asyncExecutor.runMethodsAsyncWithLatch(instance, asyncBefore, latch);
            syncExecutor.runMethodsSyncWithLatch(instance, syncBefore, latch);

            if (runBeforeCount > 0) {
                try {
                    latch.await(4L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    return;
                }
            }
            reloadInstance(configWrapper);
            asyncExecutor.runMethodsAsync(instance, asyncAfter);
            syncExecutor.runMethodsSync(instance, syncAfter);
        });
    }

    @Override
    public void registerInstance(final Object instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot register classes as instances of configuration");

        final Class<?> clazz = instance.getClass();
        Valid.validateConfigClass(clazz);

        if (instances.containsKey(clazz)) {
            throw new ConfigInstanceOverrideException(clazz);
        }
        instances.computeIfAbsent(clazz, c -> wrapInstance(instance));
    }

    @Override
    public void saveInstance(final Class<?> configClass) {
        checkNotNull(configClass, "configClass");
        validateConfigClassAndUseSerializer(configClass, (wrapper, serializer) -> serializer.saveConfig(wrapper));
    }

    @Override
    public boolean saveDefaults(final Class<?> configClass, final boolean overrideIfExists, final boolean reloadAfterwards) {
        checkNotNull(configClass, "configClass");
        final BooleanWrapper result = new BooleanWrapper();
        validateConfigClassAndUseSerializer(configClass, (wrapper, serializer) -> {
            result.set(serializer.saveDefaults(wrapper, overrideIfExists));
            if (reloadAfterwards && result.get()) reloadInstance(wrapper);
        });
        return result.get();
    }

    @SuppressWarnings("unchecked")
    private <T> void validateConfigClassAndUseSerializer(final Class<T> configClass, final BiConsumer<ConfigWrapper<T>, Serializer> consumer) {
        Valid.validateConfigClass(configClass);
        final ConfigWrapper<T> wrapper = (ConfigWrapper<T>) instances.get(configClass);
        if (wrapper == null) {
            throw new ConfigNotInitializedException(configClass);
        }
        final Serializer serializer = serializerFactory.getSerializer(wrapper.getFileType());
        consumer.accept(wrapper, serializer);
    }

    protected void reloadInstance(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
        final Serializer serializer = serializerFactory.getSerializer(configWrapper.getFileType());
        serializer.loadConfig(configWrapper);
    }
}
