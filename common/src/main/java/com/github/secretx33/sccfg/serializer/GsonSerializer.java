package com.github.secretx33.sccfg.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class GsonSerializer {

    public static Gson newInstance(final Map<Class<?>, Object> typeAdapters) {
        checkNotNull(typeAdapters, "typeAdapters cannot be null");
        final GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
        typeAdapters.forEach(builder::registerTypeAdapter);
        return builder.create();
    }
}
