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

import com.cryptomorin.xseries.XItemStack;
import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@RegisterTypeAdapter(ItemStack.class)
final class ItemStackAdapter implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {

    @Nullable
    @Override
    public JsonElement serialize(@Nullable final ItemStack item, final Type typeOfSrc, final JsonSerializationContext context) {
        if (item == null) return null;

        try {
            return serializeItemUsingXItemStack(item, context);
        } catch (final Exception e) {
            // something went wrong, try using bukkit default item serializer instead
            return serializeItemUsingBukkit(item, e);
        }
    }

    @Nullable
    @Override
    public ItemStack deserialize(@Nullable final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        if (json == null) return null;

        // serialized by bukkit default item serializer
        if (json.isJsonPrimitive()) {
            return deserializeItemUsingBukkit(json.getAsJsonPrimitive());
        }
        // serialized by XItemStack
        return deserializeItemUsingXItemStack(json, context);
    }

    private JsonElement serializeItemUsingXItemStack(final ItemStack item, final JsonSerializationContext context) {
        final ConfigurationSection section = new MemoryConfiguration();
        XItemStack.serialize(item, section);
        return context.serialize(section.getValues(false), linkedMapType);
    }

    private JsonPrimitive serializeItemUsingBukkit(final ItemStack item, final Exception e) {
        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final BukkitObjectOutputStream data = new BukkitObjectOutputStream(baos)) {
            data.writeObject(item);
            final String serializedItem = Base64.getEncoder().encodeToString(baos.toByteArray());
            return new JsonPrimitive(serializedItem);
        } catch (final IOException e2) {
            e2.addSuppressed(e);
            e2.printStackTrace();
            return new JsonPrimitive("");
        }
    }

    @Nullable
    private ItemStack deserializeItemUsingXItemStack(final JsonElement json, final JsonDeserializationContext context) throws JsonParseException {
        try {
            final Map<String, Object> map = context.deserialize(json, linkedMapType);
            final MemoryConfiguration section = new MemoryConfiguration();
            map.forEach(section::set);
            return XItemStack.deserialize(section);
        } catch (final Exception e) {
            throw new JsonParseException(e);
        }
    }

    private ItemStack deserializeItemUsingBukkit(final JsonPrimitive json) {
        final String itemAsString = json.getAsJsonPrimitive().getAsString();
        // serialization went wrong, return air instead of null
        if (itemAsString.isEmpty()) {
            return new ItemStack(Material.AIR);
        }
        // proceed with deserialization
        final byte[] serializedItem = Base64.getDecoder().decode(itemAsString);
        try (final ByteArrayInputStream bais = new ByteArrayInputStream(serializedItem);
             final BukkitObjectInputStream data = new BukkitObjectInputStream(bais)) {
            return (ItemStack) data.readObject();
        } catch (final Exception e) {
            e.printStackTrace();
            return new ItemStack(Material.AIR);
        }
    }

    private static final Type linkedMapType = new TypeToken<LinkedHashMap<String, Object>>() {}.getType();
}
