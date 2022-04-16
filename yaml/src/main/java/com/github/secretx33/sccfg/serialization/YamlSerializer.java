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

import com.github.secretx33.sccfg.config.ConfigWrapper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.IOException;
import java.util.logging.Logger;

@SuppressWarnings("unused")
public final class YamlSerializer extends AbstractConfigurateSerializer<YamlConfigurationLoader.Builder, YamlConfigurationLoader> {

    public static final int SPACES_PER_DEPTH = 2;

    public YamlSerializer(final Logger logger, final GsonProvider gsonProvider) {
        super(logger, gsonProvider);
    }

    @Override
    protected AbstractConfigurationLoader.Builder<YamlConfigurationLoader.Builder, YamlConfigurationLoader> fileBuilder(@Nullable final ConfigWrapper<?> configWrapper) {
        return YamlConfigurationLoader.builder().indent(SPACES_PER_DEPTH).nodeStyle(NodeStyle.BLOCK)
                .headerMode(HeaderMode.PRESET)
                .defaultOptions(opts -> opts.header(configWrapper != null ? configWrapper.getHeader() : null)
                        .shouldCopyDefaults(false).serializers(TypeSerializerCollection.defaults()));
    }

    @Override
    protected void afterSave(final ConfigWrapper<?> configWrapper) throws IOException {
        final YamlCommentManager commentManager = new YamlCommentManager(configWrapper);
        commentManager.saveComments();
    }
}
