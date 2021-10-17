package com.github.secretx33.sccfg.serializer.typeadapter;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@RegisterTypeAdapter(Class.class)
public class ClassAdapter implements JsonSerializer<Class<?>>, JsonDeserializer<Class<?>> {

    @Nullable
    @Override
    public Class<?> deserialize(@Nullable final JsonElement json, final Type typeOfT, JsonDeserializationContext context) {
        if (json == null) return null;
        try {
            return Class.forName(json.getAsString());
        } catch (ClassNotFoundException e) {
            throw new ConfigException(e);
        }
    }

    @Nullable
    @Override
    public JsonElement serialize(@Nullable final Class<?> src, final Type typeOfSrc, final JsonSerializationContext context) {
        if (src == null) return null;
        return context.serialize(src.getName());
    }
}
