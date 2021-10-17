package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.util.logging.Logger;

public final class YamlSerializer extends AbstractConfigurateSerializer<YamlConfigurationLoader.Builder, YamlConfigurationLoader> {

    public YamlSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    @Override
    AbstractConfigurationLoader.Builder<YamlConfigurationLoader.Builder, YamlConfigurationLoader> fileBuilder() {
        return YamlConfigurationLoader.builder().indent(2).nodeStyle(NodeStyle.BLOCK)
                .defaultOptions(opts -> opts.shouldCopyDefaults(false).serializers(TypeSerializerCollection.defaults()));
    }
}
