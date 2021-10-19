package com.github.secretx33.sccfg.serialization.gson;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.gson.typeadapter.MapDeserializerDoubleAsIntFix;
import com.github.secretx33.sccfg.util.Maps;
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
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

public class GsonFactory {

    private final Logger logger;
    private final Scanner scanner;
    private Map<Type, Object> typeAdapters = Collections.emptyMap();
    private Gson gson;
    private Gson prettyPrintGson;

    public GsonFactory(final Logger logger, final Scanner scanner) {
        this.logger = checkNotNull(logger);
        this.scanner = checkNotNull(scanner);
        parseTypeAdaptersOnClasspath();
    }

    public Gson getInstance() {
        Gson instance = gson;
        if (instance == null) {
            instance = newInstanceWithTypeAdapters(false);
            gson = instance;
        }
        return instance;
    }

    public Gson getPrettyPrintInstance() {
        Gson instance = prettyPrintGson;
        if (instance == null) {
            instance = newInstanceWithTypeAdapters(true);
            prettyPrintGson = instance;
        }
        return instance;
    }

    private void clearGsonInstances() {
        gson = null;
        prettyPrintGson = null;
    }

    public Gson newInstanceWithTypeAdapters(final boolean prettyPrint) {
        checkNotNull(typeAdapters, "typeAdapters");
        final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping()
                .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
                .setExclusionStrategies(new GsonIgnoreFieldExclusionStrategy());
        if (prettyPrint) {
            builder.setPrettyPrinting();
        }
        builder.registerTypeAdapter(GENERIC_MAP, new MapDeserializerDoubleAsIntFix());
        typeAdapters.forEach(builder::registerTypeAdapter);
        return builder.create();
    }

    public void addTypeAdapter(final Type adapterFor, final Object typeAdapter) {
        checkNotNull(adapterFor, "adapterFor");
        checkNotNull(typeAdapter, "typeAdapter");
        checkArgument(isTypeAdapter(typeAdapter.getClass()), () -> "typeAdapter passed as argument does not implement any of Gson type adapter interfaces, so I could not register " + typeAdapter.getClass().getCanonicalName() + " since it is not a type adapter");

        this.typeAdapters = Maps.immutableCopyPutting(typeAdapters, adapterFor, typeAdapter);
        clearGsonInstances();
    }

    public void addTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        notContainsNull(typeAdapters, "typeAdapters");
        if (typeAdapters.isEmpty()) return;
        checkArgument(areTypeAdapters(typeAdapters.values()), "there are at least one value on this map that is not a type adapter, please pass only type adapters as argument");

        final Map<? extends Type, Object> newTypeAdapters = typeAdapters.entrySet().stream()
                .map(entry -> {
                    final Type type = TypeToken.get(entry.getKey()).getType();
                    return new AbstractMap.SimpleEntry<>(type, entry.getValue());
                })
                .collect(Maps.toImmutableMap());
        this.typeAdapters = Maps.immutableCopyPutting(this.typeAdapters, newTypeAdapters);
        clearGsonInstances();
    }

    private void parseTypeAdaptersOnClasspath() {
        final Set<Class<?>> baseTypeAdaptersClasses = scanner.getBaseRegisterTypeAdapters();
        final Set<Class<?>> customTypeAdaptersClasses = scanner.getCustomRegisterTypeAdapters();
        final Map<Type, Object> newTypeAdapters = new HashMap<>(customTypeAdaptersClasses.size());

        baseTypeAdaptersClasses.forEach(clazz -> {
            final Class<?> annotationFor = clazz.getDeclaredAnnotation(RegisterTypeAdapter.class).value();

            try {
                final Object instance = clazz.getConstructor().newInstance();
                newTypeAdapters.put(annotationFor, checkNotNull(instance, "instance"));
            } catch (final ReflectiveOperationException e) {
                throw new IllegalStateException("This exception should not be thrown, and will only if sccfg has messed up somehow.", e);
            }
        });

        for (Class<?> clazz : customTypeAdaptersClasses) {
            final Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findAny()
                    .orElse(null);

            if (constructor == null) {
                logger.warning("Class " + clazz.getCanonicalName() + " doesn't have a no args constructor, I don't know how to instantiate it!");
                continue;
            }
            constructor.setAccessible(true);

            if (!isTypeAdapter(clazz)) {
                logger.warning("Class " + clazz.getCanonicalName() + " does not extend any of Gson typeAdapter interfaces, please double check if that class should be annotated with @RegisterTypeAdapter.");
                continue;
            }
            final RegisterTypeAdapter annotation = clazz.getDeclaredAnnotation(RegisterTypeAdapter.class);
            if (annotation == null) {
                throw new IllegalStateException("annotation should not come null at this point");
            }
            final Class<?> typeAdapterFor = annotation.value();

            try {
                newTypeAdapters.put(typeAdapterFor, constructor.newInstance());
            } catch (final ReflectiveOperationException e) {
                throw new ConfigReflectiveOperationException(e);
            }
        }
        typeAdapters = Maps.immutableOf(newTypeAdapters);
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

    private static final Type GENERIC_MAP = new TypeToken<Map<String, Object>>() {}.getType();
}
