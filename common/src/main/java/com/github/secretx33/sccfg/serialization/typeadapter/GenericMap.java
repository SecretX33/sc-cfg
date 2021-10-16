package com.github.secretx33.sccfg.serialization.typeadapter;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.factory.GsonFactory;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;

@RegisterTypeAdapter(value = Map.class)
public class GenericMap implements JsonSerializer<Map<?, ?>>, JsonDeserializer<Map<?, ?>> {

    @Override
    @Nullable
    public Map<?, ?> deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) throws JsonParseException {
        return GsonFactory.INSTANCE.fromJson(json, typeOfT);
    }

    @Override
    @Nullable
    public JsonElement serialize(@Nullable final Map<?, ?> src, final Type typeOfSrc, final JsonSerializationContext context) {
        if(src == null) return null;
        if(src.isEmpty()) return new JsonObject();
        return JsonParser.parseString(GsonFactory.INSTANCE.toJson(src, typeOfSrc));
    }
}
