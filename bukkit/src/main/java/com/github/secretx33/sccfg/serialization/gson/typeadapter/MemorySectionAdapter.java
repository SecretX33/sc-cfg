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
package com.github.secretx33.sccfg.serialization.gson.typeadapter;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@RegisterTypeAdapter(MemorySection.class)
final class MemorySectionAdapter implements JsonSerializer<MemorySection>, JsonDeserializer<MemorySection> {

    @Nullable
    @Override
    public JsonElement serialize(@Nullable final MemorySection src, final Type typeOfSrc, final JsonSerializationContext context) {
        if (src == null) return null;
        return context.serialize(configSectionToMap(src), GENERIC_MAP);
    }

    @Nullable
    @Override
    public MemorySection deserialize(@Nullable final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        if (json == null || json.isJsonNull()) return null;
        return mapToMemoryConfig(context.deserialize(json, GENERIC_MAP));
    }

    /**
     * Converts a {@code Map<?, ?>} into a {@code ConfigurationSection}.
     *
     * @param map the map to convert.
     *
     * @return a {@code ConfigurationSection} containing the map values.
     */
    private static MemoryConfiguration mapToMemoryConfig(final Map<?, ?> map) {
        final MemoryConfiguration config = new MemoryConfiguration();

        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            final String key = entry.getKey().toString();
            Object value = entry.getValue();
            if (value == null) continue;

            if (value instanceof Map<?, ?>) {
                value = mapToMemoryConfig((Map<?, ?>) value);
            }
            config.set(key, value);
        }

        return config;
    }

    /**
     * Converts a {@code ConfigurationSection} into a {@code Map<String, Object>}.
     *
     * @param config the configuration section to convert.
     *
     * @return a {@code Map<String, Object>} containing the configuration section values.
     */
    private static Map<String, Object> configSectionToMap(final ConfigurationSection config) {
        final Map<String, Object> map = new LinkedHashMap<>();

        for (final String key : config.getKeys(false)) {
            Object value = config.get(key);
            if (value == null) continue;

            if (value instanceof ConfigurationSection) {
                value = configSectionToMap((ConfigurationSection) value);
            }
            map.put(key, value);
        }

        return map;
    }

    private static final Type GENERIC_MAP = new TypeToken<Map<String, Object>>(){}.getType();
}
