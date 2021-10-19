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
import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@RegisterTypeAdapter(Class.class)
final class ClassAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Nullable
    @Override
    public Class<?> deserialize(@Nullable final JsonElement json, final Type typeOfT, JsonDeserializationContext context) {
        if (json == null) return null;
        try {
            return Class.forName(json.getAsString());
        } catch (final ClassNotFoundException e) {
            throw new ConfigDeserializationException(e);
        }
    }

    @Nullable
    @Override
    public JsonElement serialize(@Nullable final Class<?> src, final Type typeOfSrc, final JsonSerializationContext context) {
        if (src == null) return null;
        return context.serialize(src.getName());
    }
}
