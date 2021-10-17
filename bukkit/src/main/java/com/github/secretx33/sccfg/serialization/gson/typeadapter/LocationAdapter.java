package com.github.secretx33.sccfg.serialization.gson.typeadapter;

import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Optional;

@RegisterTypeAdapter(Location.class)
public class LocationAdapter implements JsonSerializer<Location>, JsonDeserializer<Location> {

    @Nullable
    @Override
    public JsonElement serialize(@Nullable final Location src, final Type typeOfSrc, final JsonSerializationContext context) {
        if(src == null) return null;
        final JsonObject object = new JsonObject();
        object.addProperty("worldName", Optional.ofNullable(src.getWorld()).map(World::getName).orElse(null));
        object.addProperty("x", src.getX());
        object.addProperty("y", src.getY());
        object.addProperty("z", src.getZ());
        return object;
    }

    @Nullable
    @Override
    public Location deserialize(@Nullable final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        if(json == null) return null;
        final JsonObject object = json.getAsJsonObject();
        final String worldName = object.get("worldName").getAsString();
        final double x = object.get("x").getAsDouble();
        final double y = object.get("y").getAsDouble();
        final double z = object.get("z").getAsDouble();
        return new Location(Bukkit.getWorld(worldName), x, y, z);
    }
}
