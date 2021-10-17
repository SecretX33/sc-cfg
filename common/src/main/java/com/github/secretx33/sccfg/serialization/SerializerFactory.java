package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;

import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SerializerFactory {

    private final Map<FileType, Serializer> serializers = new EnumMap<>(FileType.class);
    private final Logger logger;
    private final GsonFactory gsonFactory;
    private final NameMapper nameMapper = new NameMapper();

    public SerializerFactory(final Logger logger, final GsonFactory gsonFactory) {
        this.logger = checkNotNull(logger, "logger");
        this.gsonFactory = checkNotNull(gsonFactory, "gsonFactory");
    }

    public Serializer getFor(final FileType fileType) {
        checkNotNull(fileType, "fileType");
        return getSerializer(fileType);
    }

    private Serializer getSerializer(final FileType fileType) {
        switch(fileType) {
            case JSON:
                return serializers.computeIfAbsent(fileType, type -> new JsonSerializer(logger, gsonFactory));
            case YAML:
                return serializers.computeIfAbsent(fileType, type -> new YamlSerializer(logger, gsonFactory));
            default:
                throw new ConfigException("Oops, I don't know how to serialize " + fileType + ".");
        }
    }

    public NameMapper getNameMapper() {
        return nameMapper;
    }
}
