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
package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.Naming;
import com.github.secretx33.sccfg.api.annotation.Comment;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.Name;
import com.github.secretx33.sccfg.api.annotation.NamedPath;
import com.github.secretx33.sccfg.api.annotation.PathComment;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigInstanceOverrideException;
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException;
import com.github.secretx33.sccfg.executor.AsyncExecutor;
import com.github.secretx33.sccfg.executor.AsyncMethodExecutor;
import com.github.secretx33.sccfg.executor.SyncExecutor;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.GsonProvider;
import com.github.secretx33.sccfg.serialization.Serializer;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapperFactory;
import com.github.secretx33.sccfg.storage.FileModificationType;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import com.github.secretx33.sccfg.util.BooleanWrapper;
import com.github.secretx33.sccfg.util.ClassUtil;
import com.github.secretx33.sccfg.util.Maps;
import com.github.secretx33.sccfg.util.Sets;
import com.github.secretx33.sccfg.util.Valid;
import com.google.common.collect.ObjectArrays;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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
    private final AsyncExecutor asyncExecutor;
    private final SyncExecutor syncExecutor;
    private final SerializerFactory serializerFactory;
    private final NameMapperFactory nameMapperFactory;

    public ConfigFactoryImpl(
            final Logger logger,
            final GsonProvider gsonProvider,
            final Path basePath,
            final Scanner scanner,
            final FileWatcher fileWatcher,
            final SyncExecutor syncExecutor
    ) {
        this(
            basePath,
            scanner,
            fileWatcher,
            new AsyncMethodExecutor(logger),
            syncExecutor,
            new SerializerFactory(logger, gsonProvider),
            new NameMapperFactory()
        );
    }

    public ConfigFactoryImpl(
            final Path basePath,
            final Scanner scanner,
            final FileWatcher fileWatcher,
            final AsyncExecutor asyncExecutor,
            final SyncExecutor syncExecutor,
            final SerializerFactory serializerFactory,
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
    public <T> ConfigWrapper<T> getWrapper(final Class<T> configClass) {
        checkNotNull(configClass, "configClass");
        return (ConfigWrapper<T>) instances.computeIfAbsent(configClass, this::createWrappedConfigInstance);
    }

    private <T> ConfigWrapper<T> createWrappedConfigInstance(final Class<T> clazz) {
        Valid.ensureInstantiableConfigClass(clazz);
        final Constructor<T> constructor = ClassUtil.zeroArgsConstructor(clazz);
        try {
            return wrapInstance(constructor.newInstance());
        } catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
            throw new ConfigException(e);
        }
    }

    private <T> ConfigWrapper<T> wrapInstance(final T instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot register classes as instances of configuration");

        try {
            final Class<?> clazz = instance.getClass();
            final Configuration annotation = ClassUtil.configAnnotation(clazz);
            final Serializer serializer = serializerFactory.getSerializer(annotation.type());
            final Set<Field> configFields = scanner.getConfigurationFields(clazz);
            final Set<PropertyWrapper> properties = mapConfigFieldsToProperties(instance, configFields, annotation.naming());
            final Map<String, String[]> comments = getConfigComments(clazz, properties, configFields);
            final Path configPath = Paths.get(parseConfigPath(clazz, annotation));
            final FileWatcher.WatchedLocation watchedLocation = fileWatcher.getWatcher(configPath);

            final ConfigWrapper<T> wrapper = ConfigWrapperImpl.builder()
                .instance(instance)
                .configAnnotation(annotation)
                .destination(basePath.resolve(configPath))
                .defaults(serializer.getCurrentValues(instance, properties))
                .properties(properties)
                .comments(comments)
                .runBeforeReloadMethods(scanner.getBeforeReloadMethods(clazz))
                .runAfterReloadMethods(scanner.getAfterReloadMethods(clazz))
                .watchedLocation(watchedLocation)
                .build();

            watchedLocation.addListener(FileModificationType.CREATE_AND_MODIFICATION, handleReload(wrapper));
            return serializer.loadConfig(wrapper);
        } catch (final ConfigException e) {
            throw e;
        } catch (final Exception e) {
            throw new ConfigException(e);
        }
    }

    private Set<PropertyWrapper> mapConfigFieldsToProperties(final Object instance, final Set<Field> fields, final Naming naming) {
        checkNotNull(instance, "instance");
        notContainsNull(fields, "fields");
        checkNotNull(naming, "naming");

        final NameMapper mapper = nameMapperFactory.getMapper(naming);

        return fields.stream().sequential().map(field -> {
            final NamedPath namedPathAnnotation = field.getDeclaredAnnotation(NamedPath.class);
            final com.github.secretx33.sccfg.api.annotation.Path pathAnnotation = field.getDeclaredAnnotation(com.github.secretx33.sccfg.api.annotation.Path.class);
            String path = firstPresent(Optional.ofNullable(pathAnnotation).map(com.github.secretx33.sccfg.api.annotation.Path::value), Optional.ofNullable(namedPathAnnotation).map(NamedPath::path));

            if (path == null) {
                path = "";
            } else {
                checkNotBlank(path, () -> "@Path annotation does not support null, empty or blank values, but you passed one of these three as parameter on your field " + field.getName() + " (which belongs to class " + field.getDeclaringClass().getSimpleName() + ")");
            }

            final Name nameAnnotation = field.getDeclaredAnnotation(Name.class);
            String nameOnFile = firstPresent(Optional.ofNullable(nameAnnotation).map(Name::value), Optional.ofNullable(namedPathAnnotation).map(NamedPath::name));

            if (nameOnFile == null) {
                nameOnFile = mapper.applyStrategy(field.getName());
            } else {
                checkNotBlank(nameOnFile, () -> "@Name annotation does not support null, empty or blank values, but you passed one of these three as value of @Name annotation on your field '" + field.getName() + "' (which belongs to class '" + field.getDeclaringClass().getSimpleName() + "')");
            }

            final Comment commentAnnotation = field.getDeclaredAnnotation(Comment.class);
            String[] comment = firstPresent(Optional.ofNullable(commentAnnotation).map(Comment::value), Optional.ofNullable(namedPathAnnotation).map(NamedPath::comment));

            if (comment == null) {
                comment = new String[0];
            }
            return new PropertyWrapperImpl(instance, field, nameOnFile, path, comment);
        }).collect(Sets.toSet());
    }

    @SafeVarargs
    @Nullable
    private final <T> T firstPresent(final Optional<T>... strings) {
        for (final Optional<T> string : strings) {
            if (string.isPresent()) {
                return string.get();
            }
        }
        return null;
    }

    private Map<String, String[]> getConfigComments(
            final Class<?> clazz,
            final Collection<PropertyWrapper> properties,
            final Collection<Field> configFields
    ) {
        final Map<String, String[]> map = new LinkedHashMap<>();

        // from Comment and NamedPath annotations
        properties.stream()
            .filter(property -> property.hasComment() && property.getComment() != null)
            .forEach(property -> map.put(property.getFullPathOnFile(), property.getComment().split("\\n")));

        // from PathComment and PathComments annotations
        final Collection<PathComment> pathComment = scanner.getPathCommentFromClassAndAllFields(clazz, configFields);
        pathComment.forEach(annotation -> {
            final String[] annotationComment = String.join("\n", annotation.comment()).split("\n");
            checkNotBlank(annotation.path(), () -> String.format("@PathComment annotation cannot hold empty or blank paths, but class '%s' comment '%s' have a empty/blank path assigned to it!", clazz.getCanonicalName(), Arrays.toString(annotationComment)));
            map.compute(annotation.path(), (k, v) -> (v == null) ? annotationComment : ObjectArrays.concat(v, annotationComment, String.class));
        });
        return Maps.of(map);
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

            asyncExecutor.execute(instance, asyncBefore, latch);
            syncExecutor.execute(instance, syncBefore, latch);

            if (runBeforeCount > 0) {
                try {
                    latch.await(4L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    return;
                }
            }
            reloadInstance(configWrapper);
            asyncExecutor.execute(instance, asyncAfter);
            syncExecutor.execute(instance, syncAfter);
        });
    }

    @Override
    public void registerInstance(final Object instance) {
        checkNotNull(instance, "instance");
        checkArgument(!(instance instanceof Class<?>), "cannot register classes as instances of configuration");

        final Class<?> clazz = Valid.ensureConfigClass(instance.getClass());

        instances.compute(clazz, (key, oldValue) -> {
            if (oldValue != null) {  // throw if there is already an instance registered for given class
                throw new ConfigInstanceOverrideException(clazz);
            }
            return wrapInstance(key);
        });
    }

    @Override
    public void saveInstance(final Class<?> configClass) {
        checkNotNull(configClass, "configClass");
        withWrapperAndSerializer(configClass, (wrapper, serializer) -> serializer.saveConfig(wrapper));
    }

    @Override
    public boolean saveDefaults(final Class<?> configClass, final boolean overrideIfExists, final boolean reloadAfterwards) {
        checkNotNull(configClass, "configClass");
        final BooleanWrapper result = new BooleanWrapper();
        withWrapperAndSerializer(configClass, (wrapper, serializer) -> {
            result.set(serializer.saveDefaults(wrapper, overrideIfExists));
            if (reloadAfterwards && result.get()) reloadInstance(wrapper);
        });
        return result.get();
    }

    @SuppressWarnings("unchecked")
    private <T> void withWrapperAndSerializer(final Class<T> configClass, final BiConsumer<ConfigWrapper<T>, Serializer> consumer) {
        Valid.ensureConfigClass(configClass);
        final ConfigWrapper<T> wrapper = (ConfigWrapper<T>) instances.get(configClass);
        if (wrapper == null) {
            throw new ConfigNotInitializedException(configClass);
        }
        final Serializer serializer = serializerFactory.getSerializer(wrapper.getFileType());
        consumer.accept(wrapper, serializer);
    }

    private void reloadInstance(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
        final Serializer serializer = serializerFactory.getSerializer(configWrapper.getFileType());
        serializer.loadConfig(configWrapper);
    }
}
