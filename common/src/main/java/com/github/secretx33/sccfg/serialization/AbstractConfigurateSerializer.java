package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.util.Maps;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractConfigurateSerializer<U extends AbstractConfigurationLoader.Builder<U, L>, L extends AbstractConfigurationLoader<?>> extends AbstractSerializer {

    public AbstractConfigurateSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    abstract AbstractConfigurationLoader.Builder<U, L> fileBuilder();

    @Override
    @SuppressWarnings("unchecked")
    protected Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper) {
        final Path path = configWrapper.getDestination();
        final Object file;

        try {
            file = fileBuilder().path(path).build().load().raw();
        } catch (final ConfigurateException e) {
            logger.log(Level.SEVERE, "An error has occurred when deserializing file '" + configWrapper.getDestination().getFileName() + "' from " + configWrapper.getFileType() + ". There is probably some kind of typo on it, so it could not be parsed, please fix any typos on the file.", new ConfigDeserializationException(e));
            return Collections.emptyMap();
        }

        if (!(file instanceof Map)) {
            return Collections.emptyMap();
        }
        return Maps.immutableOf((Map<String, Object>)file);
    }

    @Override
    protected void saveToFile(final ConfigWrapper<?> configWrapper, final Map<String, Object> newValues) {
        final Path path = configWrapper.getDestination();
        final String json;

        try {
            json = gsonFactory.getInstance().toJson(newValues);
        } catch (final Exception e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing config class " + configWrapper.getInstance().getClass().getName(), ex);
            throw ex;
        }

        final ConfigurationNode fileNode;
        try {
            fileNode = fileBuilder().buildAndLoadString(json);
        } catch (final ConfigurateException e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when converting the config class " + configWrapper.getInstance().getClass().getName() + " to " + configWrapper.getFileType() + ".", ex);
            throw ex;
        }

        try {
            fileBuilder().path(path).build().save(fileNode);
        } catch (final ConfigurateException e) {
            logger.log(Level.SEVERE, "An error has occurred when saving your config file '" + configWrapper.getInstance().getClass().getName() + " to the disk.", e);
            throw new ConfigException(e);
        }
    }
}
