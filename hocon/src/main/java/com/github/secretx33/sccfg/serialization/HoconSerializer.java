/*
 * Copyright (C) 2021 SecretX <notyetmidnight@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import java.util.logging.Logger;

@SuppressWarnings("unused")
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