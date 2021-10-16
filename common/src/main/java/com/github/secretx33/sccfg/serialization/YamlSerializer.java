package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.factory.GsonFactory;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class YamlSerializer extends AbstractSerializer {

    public YamlSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Path path = configWrapper.getDestination();
        saveDefault(configWrapper);

    }

    @Override
    public void saveConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        createFileIfMissing(config, path);
        saveToFile(configWrapper, path, config);
    }

    @Override
    public boolean saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Object config = configWrapper.getInstance();
        final Path path = configWrapper.getDestination();
        if(!createFileIfMissing(config, path)) return false;
        saveToFile(configWrapper, path, configWrapper.getDefaults());
        return true;
    }

    private void saveToFile(final ConfigWrapper<?> configWrapper, final Path path, final Object newValues) {
        final String json;
        try {
            json = gsonFactory.getInstance().toJson(newValues);
        } catch (final Exception e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing config class " + configWrapper.getInstance().getClass().getName() + ".", ex);
            throw ex;
        }

        final ConfigurationNode yaml;
        try {
            yaml = yamlBuilder().buildAndLoadString(json);
        } catch (final ConfigurateException e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when converting the config class " + configWrapper.getInstance().getClass().getName() + " to YAML.", ex);
            throw ex;
        }

        try {
            yamlBuilder().path(path).build().save(yaml);
        } catch (final ConfigurateException e) {
            throw new ConfigException(e);
        }
    }

    private YamlConfigurationLoader.Builder yamlBuilder() {
        return YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(defaults -> defaults.shouldCopyDefaults(false));
    }
}
