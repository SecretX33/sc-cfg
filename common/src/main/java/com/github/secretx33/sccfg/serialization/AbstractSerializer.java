package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.factory.GsonFactory;
import com.github.secretx33.sccfg.util.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public Map<String, ?> getDefaults(Object configInstance) {
        try {
            final Gson gson = gsonFactory.getInstance();
            return Maps.immutableOf(gson.fromJson(gson.toJson(configInstance), mapToken));
        } catch (final Exception e) {
            throw new ConfigSerializationException(e);
        }
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

    @SuppressWarnings("UnstableApiUsage")
    private static final Type mapToken = new TypeToken<Map<String, ?>>() {}.getType();
}
