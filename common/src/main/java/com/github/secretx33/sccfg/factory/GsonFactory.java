package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.MissingTypeOverrideOnAdapter;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.TypeAdapter;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class GsonFactory {

    private final Logger logger;
    private final Scanner scanner;
    private final Map<Class<?>, Object> typeAdapters = new HashMap<>();
    private Gson gson;

    public GsonFactory(final Logger logger, final Scanner scanner) {
        this.logger = checkNotNull(logger);
        this.scanner = checkNotNull(scanner);
        parseTypeAdaptersOnClasspath();
    }

    public Gson getInstance() {
        if (gson == null) {
            gson = newInstanceWithTypeAdapters();
        }
        return gson;
    }

    public Gson newInstanceWithTypeAdapters() {
        checkNotNull(typeAdapters, "typeAdapters cannot be null");
        final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        typeAdapters.forEach(builder::registerTypeAdapter);
        return builder.create();
    }

    public void addCustomTypeAdapter(final Class<?> adapterFor, Object typeAdapter) {
        checkNotNull(adapterFor, "adapterFor cannot be null");
        checkNotNull(typeAdapter, "typeAdapter cannot be null");
        checkArgument(isTypeAdapter(typeAdapter.getClass()), () -> "typeAdapter passed as argument does not implement any of Gson type adapter interfaces, so I could not register " + typeAdapter.getClass().getCanonicalName() + " since it is not a type adapter");
        this.typeAdapters.put(adapterFor, typeAdapter);
    }

    public void addCustomTypeAdapters(final Map<Class<?>, Object> typeAdapters) {
        checkNotNull(typeAdapters);
        this.typeAdapters.putAll(typeAdapters);
    }

    private void parseTypeAdaptersOnClasspath() {
        final Set<Class<?>> typeAdaptersClasses = scanner.getRegisterTypeAdapters();

        for (Class<?> clazz : typeAdaptersClasses) {
            final Constructor<?> constructor = Arrays.stream(clazz.getDeclaredConstructors())
                    .filter(c -> c.getParameterCount() == 0)
                    .findAny()
                    .orElse(null);

            if(constructor == null) {
                logger.warning("Class " + clazz.getCanonicalName() + " doesn't have a no args constructor, I don't know how to instantiate it!");
                continue;
            }
            constructor.setAccessible(true);

            if(!isTypeAdapter(clazz)) {
                logger.warning("Class " + clazz.getCanonicalName() + " does not extend any of Gson typeAdapter interfaces, please double check if that class should be annotated with @RegisterTypeAdapter.");
                continue;
            }
            final RegisterTypeAdapter annotation = clazz.getDeclaredAnnotation(RegisterTypeAdapter.class);
            if(annotation == null) {
                throw new IllegalStateException("annotation should not come null at this point");
            }

            final Class<?> annotationFor = annotation.value();
            if(annotationFor.equals(Object.class)) {
                throw new MissingTypeOverrideOnAdapter(clazz);
            }

            try {
                typeAdapters.put(annotationFor, constructor.newInstance());
            } catch (final ReflectiveOperationException e) {
                throw new ConfigException(e);
            }
        }
    }

    private boolean isTypeAdapter(final Class<?> clazz) {
        return JsonSerializer.class.isAssignableFrom(clazz)
                || JsonDeserializer.class.isAssignableFrom(clazz)
                || InstanceCreator.class.isAssignableFrom(clazz)
                || TypeAdapter.class.isAssignableFrom(clazz);
    }
}
