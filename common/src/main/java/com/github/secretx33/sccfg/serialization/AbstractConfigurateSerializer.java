package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;
import com.github.secretx33.sccfg.util.Maps;
import com.google.gson.JsonSyntaxException;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;

import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractConfigurateSerializer<U extends AbstractConfigurationLoader.Builder<U, L>, L extends AbstractConfigurationLoader<?>> extends AbstractSerializer {

    public AbstractConfigurateSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    abstract AbstractConfigurationLoader.Builder<U, L> fileBuilder();

    @Override
    public <T> ConfigWrapper<T> loadConfig(final ConfigWrapper<T> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        saveDefault(configWrapper);
        final Object instance = configWrapper.getInstance();
        final NameMap nameMap = configWrapper.getNameMap();
        final Map<String, Object> fileValues = loadFromFile(configWrapper);

        configWrapper.getConfigFields().stream()
                .filter(field -> nameMap.getFileEquivalent(field.getName()) != null)
                .filter(field -> fileValues.get(nameMap.getFileEquivalent(field.getName())) != null)
                .forEach(field -> {
                    final Object newValue = fileValues.get(nameMap.getFileEquivalent(field.getName()));
                    try {
                        setValueOnField(instance, field, newValue);
                    } catch (final IllegalArgumentException | JsonSyntaxException e) {
                        // field type does not match the value deserialized
                        logger.warning("Could not deserialize config field '" + field.getName() + "' from file '" + configWrapper.getDestination().getFileName() + "' because the deserialized type '" + newValue.getClass().getSimpleName() + "' does not match the expected type '" + field.getType().getSimpleName() + "'. That usually happens when you make a typo in your configuration file, so please check out that config field and correct any mistakes.");
                    } catch (final IllegalAccessException e) {
                        throw new ConfigException(e);
                    }
                });
        return configWrapper;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> loadFromFile(final ConfigWrapper<?> configWrapper) {
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
    public void saveConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        createFileIfMissing(config, path);
        saveToFile(configWrapper, config);
    }

    @Override
    public boolean saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        if(!createFileIfMissing(config, path)) return false;
        saveToFile(configWrapper, configWrapper.getDefaults());
        return true;
    }

    private void saveToFile(final ConfigWrapper<?> configWrapper, final Object newValues) {
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
            throw new ConfigException(e);
        }
    }
}
