package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.logging.Logger;

public final class HoconSerializer extends AbstractConfigurateSerializer<HoconConfigurationLoader.Builder, HoconConfigurationLoader> {

    public HoconSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    @Override
    AbstractConfigurationLoader.Builder<HoconConfigurationLoader.Builder, HoconConfigurationLoader> fileBuilder() {
        return HoconConfigurationLoader.builder().prettyPrinting(true)
                .emitComments(true)
                .emitJsonCompatible(false)
                .defaultOptions(opts -> opts.shouldCopyDefaults(false).serializers(TypeSerializerCollection.defaults()));
    }
}
