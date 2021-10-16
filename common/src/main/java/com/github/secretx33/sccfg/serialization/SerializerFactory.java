package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Map<FileType, Serializer> serializers = new EnumMap<>(FileType.class);
    private final Logger logger;

    public SerializerFactory(final Logger logger) {
        this.logger = checkNotNull(logger, "logger cannot be null");
    }

    public Serializer getFor(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper cannot be null");
        return getSerializer(configWrapper.getFileType());
    }

    private Serializer getSerializer(final FileType fileType) {
        switch(fileType) {
            case JSON:
                return serializers.computeIfAbsent(fileType, type -> new JsonSerializer(logger));
            case YAML:
                return serializers.computeIfAbsent(fileType, type -> new YamlSerializer(logger));
            default:
                throw new ConfigException("Oops, I don't know how to serialize " + fileType + ".");
        }
    }
}
