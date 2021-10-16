package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class YamlSerializer implements Serializer {

    private final Logger logger;

    public YamlSerializer(final Logger logger) {
        this.logger = checkNotNull(logger, "logger");
    }

    @Override
    public void loadConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Path path = configWrapper.getDestination();
        try {
            Files.createFile(path);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An error has occurred when saving " + configWrapper.getInstance().getClass().getName() + ".", e);
        }
    }

    @Override
    public boolean saveConfig(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");

        final Path path = configWrapper.getDestination();
        try {
            if (Files.notExists(path)) {
                Files.createFile(path);
            }
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An error has occurred when creating config file for class " + configWrapper.getInstance().getClass().getName() + ".", e);
            return false;
        }
        return true;

//        YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK).path(path)
//                .defaultOptions()
//                .build();

//        JsonNode jsonNodeTree = new ObjectMapper().readTree(jsonString);
//        return new YAMLMapper().writeValueAsString(jsonNodeTree);
    }

    @Override
    public void saveDefault(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
    }
}
