package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;
import com.github.secretx33.sccfg.util.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractSerializer implements Serializer {

    protected final Logger logger;
    protected final GsonFactory gsonFactory;

    public AbstractSerializer(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    @Override
    public Map<String, Object> getCurrentValues(final Object configInstance, final NameMap nameMap) {
        checkNotNull(configInstance, "configInstance");
        final Gson gson = gsonFactory.getInstance();
        final Map<String, Object> defaults;
        try {
            defaults = gson.fromJson(gson.toJson(configInstance), linkedMapToken);
        } catch (final Exception e) {
            throw new ConfigSerializationException("sc-cfg doesn't know how to serialize a field in your config class '" + configInstance.getClass().getName() + "', consider adding a Type Adapter for your custom types", e);
        }
        // map java names to file names
        return defaults.entrySet().stream().sequential()
                .filter(entry -> nameMap.getFileEquivalent(entry.getKey()) != null)
                .map(entry -> new AbstractMap.SimpleEntry<>(nameMap.getFileEquivalent(entry.getKey()), entry.getValue()))
                .collect(Maps.toImmutableLinkedMap());
    }

    protected boolean createFileIfMissing(final Object configInstance, final Path path) {
        checkNotNull(configInstance, "configInstance");
        checkNotNull(path, "path");

        if (Files.exists(path)) return false;
        try {
            final Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.createFile(path);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An error has occurred when creating config file for class " + configInstance.getClass().getName() + ".", e);
            throw new ConfigException(e);
        }
        return true;
    }

    protected void setValueOnField(final Object instance, final Field field, final Object value) throws IllegalAccessException, JsonSyntaxException {
        final Class<?> requiredType = field.getType();
        final Class<?> providedType = value.getClass();

        if (!requiredType.equals(providedType) && !requiredType.isAssignableFrom(providedType)) {
            final Gson gson = gsonFactory.getInstance();
            field.set(instance, gson.fromJson(gson.toJson(value), requiredType));
            return;
        }
        field.set(instance, value);
    }

    @SuppressWarnings("UnstableApiUsage")
    protected static final Type linkedMapToken = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
}
