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
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.gson.GsonIgnoreFieldExclusionStrategy;
import com.github.secretx33.sccfg.serialization.typeadapter.MapDeserializerDoubleAsIntFix;
import com.github.secretx33.sccfg.util.ClassUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

public final class GsonProviderImpl implements GsonProvider {

    private final Logger logger;
    private final Scanner scanner;
    private final Map<Type, Object> typeAdapters = new ConcurrentHashMap<>();
    private volatile Gson gson;

    public GsonProviderImpl(final Logger logger, final Scanner scanner) {
        this.logger = checkNotNull(logger, "logger");
        this.scanner = checkNotNull(scanner, "scanner");
        findAndRegisterTypeAdaptersOnClasspath();
    }

    @Override
    public Gson getInstance() {
        Gson instance = gson;
        if (instance != null) return instance;
        synchronized (this) {
            instance = gson;
            if (instance == null) {
                instance = createGson();
                gson = instance;
            }
            return instance;
        }
    }

    private void removeCachedGson() {
        gson = null;
    }

    private Gson createGson() {
        checkNotNull(typeAdapters, "typeAdapters");
        final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                .setExclusionStrategies(new GsonIgnoreFieldExclusionStrategy())
                .registerTypeAdapter(GENERIC_MAP_TOKEN, new MapDeserializerDoubleAsIntFix());
        typeAdapters.forEach(builder::registerTypeAdapter);
        return builder.create();
    }

    @Override
    public void addTypeAdapter(final Type adapterFor, final Object typeAdapter) {
        checkNotNull(adapterFor, "adapterFor");
        checkNotNull(typeAdapter, "typeAdapter");
        checkArgument(isTypeAdapter(typeAdapter.getClass()), () -> "typeAdapter passed as argument does not implement any of Gson type adapter interfaces, so I could not register " + typeAdapter.getClass().getCanonicalName() + " since it is not a type adapter");

        this.typeAdapters.put(adapterFor, typeAdapter);
        removeCachedGson();
    }

    @Override
    public void addTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        notContainsNull(typeAdapters, "typeAdapters");
        if (typeAdapters.isEmpty()) return;
        checkArgument(areTypeAdapters(typeAdapters.values()), "there are at least one value on the typeAdapters that is not a type adapter, please pass only type adapters as argument");

        this.typeAdapters.putAll(typeAdapters);
        removeCachedGson();
    }

    private void findAndRegisterTypeAdaptersOnClasspath() {
        final Set<Class<?>> defaultTypeAdaptersClasses = scanner.findDefaultRegisterTypeAdapters();
        final Set<Class<?>> customTypeAdaptersClasses = scanner.findCustomRegisterTypeAdapters();
        final Map<Type, Object> newTypeAdapters = new HashMap<>(customTypeAdaptersClasses.size());

        defaultTypeAdaptersClasses.forEach(clazz -> {
            final Class<?> annotationFor = clazz.getDeclaredAnnotation(RegisterTypeAdapter.class).value();

            try {
                final Constructor<?> constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                final Object instance = constructor.newInstance();
                newTypeAdapters.put(annotationFor, checkNotNull(instance, "instance"));
            } catch (final ReflectiveOperationException e) {
                throw new ConfigInternalErrorException("This exception should not be thrown, and will only if sc-cfg has messed up its default type adapters.", e);
            }
        });

        for (Class<?> clazz : customTypeAdaptersClasses) {
            final Constructor<?> constructor = ClassUtil.zeroArgsConstructor(clazz);

            if (!isTypeAdapter(clazz)) {
                logger.warning("Class " + clazz.getCanonicalName() + " does not extend any of Gson typeAdapter interfaces, please double check if that class should be annotated with @RegisterTypeAdapter.");
                continue;
            }
            final RegisterTypeAdapter annotation = clazz.getDeclaredAnnotation(RegisterTypeAdapter.class);
            if (annotation == null) {
                throw new ConfigInternalErrorException("annotation should not come null at this point");
            }
            final Class<?> typeAdapterFor = annotation.value();

            try {
                newTypeAdapters.put(typeAdapterFor, constructor.newInstance());
            } catch (final ReflectiveOperationException e) {
                throw new ConfigReflectiveOperationException(e);
            }
        }
        addTypeAdapters(newTypeAdapters);
    }

    private boolean isTypeAdapter(final Class<?> clazz) {
        return JsonSerializer.class.isAssignableFrom(clazz)
                || JsonDeserializer.class.isAssignableFrom(clazz)
                || InstanceCreator.class.isAssignableFrom(clazz)
                || TypeAdapter.class.isAssignableFrom(clazz);
    }

    private boolean areTypeAdapters(final Collection<?> typeAdapters) {
        return typeAdapters.stream().allMatch(adapter -> isTypeAdapter(adapter.getClass()));
    }

    private static final Type GENERIC_MAP_TOKEN = new TypeToken<Map<String, Object>>() {}.getType();
}
