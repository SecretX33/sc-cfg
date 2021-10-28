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

import com.github.secretx33.sccfg.api.NameStrategy;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException;
import com.github.secretx33.sccfg.exception.ConfigOverrideException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;
import com.github.secretx33.sccfg.executor.AsyncExecutor;
import com.github.secretx33.sccfg.executor.SyncExecutor;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.Serializer;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapperFactory;
import com.github.secretx33.sccfg.storage.FileModificationType;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import com.github.secretx33.sccfg.util.Sets;
import com.github.secretx33.sccfg.util.Valid;
import com.github.secretx33.sccfg.wrapper.ConfigEntry;
import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.github.secretx33.sccfg.wrapper.MethodWrapper;

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
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BaseConfigFactory implements ConfigFactory {

    private final Map<Class<?>, ConfigWrapper<?>> instances = new ConcurrentHashMap<>();
    private final Path basePath;
    private final Scanner scanner;
    private final FileWatcher fileWatcher;
    private final SerializerFactory serializerFactory;
    private final AsyncExecutor asyncExecutor;
    private final SyncExecutor syncExecutor;
    private final NameMapperFactory nameMapperFactory;

    public BaseConfigFactory(
            final Path basePath,
            final Scanner scanner,
            final FileWatcher fileWatcher,
            final SerializerFactory serializerFactory,
            final AsyncExecutor asyncExecutor,
            final SyncExecutor syncExecutor,
            final NameMapperFactory nameMapperFactory
    ) {
        this.basePath = checkNotNull(basePath, "basePath");
        this.scanner = checkNotNull(scanner, "scanner");
        this.fileWatcher = checkNotNull(fileWatcher, "fileWatcher");
        this.serializerFactory = checkNotNull(serializerFactory, "serializerFactory");
        this.asyncExecutor = checkNotNull(asyncExecutor, "asyncExecutor");
        this.syncExecutor = checkNotNull(syncExecutor, "syncExecutor");
        this.nameMapperFactory = checkNotNull(nameMapperFactory, "nameMapperFactory");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> ConfigWrapper<T> getWrapper(final Class<T> configClazz) {
        checkNotNull(configClazz, "configClazz");
        return (ConfigWrapper<T>) instances.computeIfAbsent(configClazz, this::newWrappedConfigInstance);
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
        final Serializer serializer = serializerFactory.getSerializer(annotation.type());
        final Set<Field> configFields = scanner.getConfigurationFields(clazz);
        final Set<ConfigEntry> configEntries = mapConfigEntries(instance, configFields, annotation.nameStrategy());
        final Map<String, Object> defaults = serializer.getCurrentValues(instance, configEntries);
        try {
            final Path configPath = Paths.get(parseConfigPath(clazz, annotation));
            final Path destination = basePath.resolve(configPath);
            final Set<MethodWrapper> runBeforeReload = scanner.getBeforeReloadMethods(clazz);
            final Set<MethodWrapper> runAfterReload = scanner.getAfterReloadMethods(clazz);

            final FileWatcher.WatchedLocation watchedLocation = fileWatcher.getWatcher(configPath);
            final ConfigWrapper<T> wrapper = new ConfigWrapper<>(instance, annotation, destination, defaults, configEntries, runBeforeReload, runAfterReload, watchedLocation);
            watchedLocation.addListener(FileModificationType.CREATE_AND_MODIFICATION, handleReload(wrapper));
            watchedLocation.recordChange(destination);
            return serializer.loadConfig(wrapper);
        }  catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
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

    private Set<ConfigEntry> mapConfigEntries(final Object instance, final Set<Field> fields, final NameStrategy strategy) {
        final NameMapper mapper = nameMapperFactory.getMapper(strategy);

        return fields.stream().sequential().map(field -> {
            final com.github.secretx33.sccfg.api.annotation.Path pathAnnotation = field.getDeclaredAnnotation(com.github.secretx33.sccfg.api.annotation.Path.class);
            final String path;
            if (pathAnnotation == null) {
                path = "";
            } else {
                path = pathAnnotation.value();
                checkNotBlank(path, () -> "@Path annotation does not support null, empty or blank values, but you passed one of these three as parameter on your field " + field.getName() + " (which belongs to class " + field.getDeclaringClass().getSimpleName() + ")");
            }
            final String nameOnFile = mapper.applyStrategy(field.getName());
            return new ConfigEntry(instance, field, nameOnFile, path);
        }).collect(Sets.toSet());
    }

    private String parseConfigPath(final Class<?> clazz, final Configuration configuration) {
        final String value = configuration.value().trim();
        final String lowerCasedValue = value.toLowerCase(Locale.US);
        final String extension = configuration.type().extension;
        if (value.isEmpty()) {
            return clazz.getName() + extension;
        }
        if(lowerCasedValue.endsWith(extension)) {
            return value.substring(0, lowerCasedValue.lastIndexOf(extension)) + extension;
        }
        return value + extension;
    }

    @SuppressWarnings("unchecked")
    private <T> Constructor<T> getDefaultConstructor(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny()
                .map(c -> (Constructor<T>)c)
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

            if(runBeforeCount > 0) {
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

        if(instances.containsKey(clazz)) {
            throw new ConfigOverrideException(clazz);
        }
        final ConfigWrapper<?> wrapper = wrapInstance(instance);
        instances.put(clazz, wrapper);
    }

    @Override
    public void saveInstance(final Object instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot save instance of clazz");
        saveInstance(instance.getClass());
    }

    @Override
    public void saveInstance(final Class<?> configClazz) {
        checkNotNull(configClazz, "configClazz");

        Valid.validateConfigClass(configClazz);
        final ConfigWrapper<?> wrapper = instances.get(configClazz);
        if (wrapper == null) {
            throw new ConfigNotInitializedException(configClazz);
        }
        final Serializer serializer = serializerFactory.getSerializer(wrapper.getFileType());
        serializer.saveConfig(wrapper);
    }

    protected void reloadInstance(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
        final Serializer serializer = serializerFactory.getSerializer(configWrapper.getFileType());
        serializer.loadConfig(configWrapper);
    }
}
