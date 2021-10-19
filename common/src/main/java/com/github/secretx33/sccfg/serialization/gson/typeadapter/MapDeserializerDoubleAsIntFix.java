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

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapDeserializerDoubleAsIntFix implements JsonDeserializer<Map<String, Object>> {

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> deserialize(@Nullable final JsonElement json, final Type typeOfT, final JsonDeserializationContext context) {
        return (Map<String, Object>) read(json);
    }

    @Nullable
    public Object read(@Nullable final JsonElement in) {
        if (in == null) return null;

        if (in.isJsonArray()) {
            final List<Object> list = new ArrayList<>();
            final JsonArray arr = in.getAsJsonArray();
            for (JsonElement anArr : arr) {
                list.add(read(anArr));
            }
            return list;
        }
        if (in.isJsonObject()) {
            final Map<String, Object> map = new LinkedTreeMap<>();
            final JsonObject obj = in.getAsJsonObject();
            final Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
            for (Map.Entry<String, JsonElement> entry : entitySet) {
                map.put(entry.getKey(), read(entry.getValue()));
            }
            return map;
        }
        if (in.isJsonPrimitive()) {
            final JsonPrimitive prim = in.getAsJsonPrimitive();
            if (prim.isBoolean()) {
                return prim.getAsBoolean();
            }
            if (prim.isString()) {
                return prim.getAsString();
            }
            if (prim.isNumber()) {
                final Number num = prim.getAsNumber();
                final double doubleValue = num.doubleValue();

                if (doubleValue == num.byteValue()) {
                    return num.byteValue();
                } else if (doubleValue == num.shortValue()) {
                    return num.shortValue();
                } else if (doubleValue == num.intValue()) {
                    return num.intValue();
                } else if (doubleValue == num.longValue()) {
                    return num.longValue();
                } else if (Double.parseDouble(String.valueOf(num.floatValue())) == doubleValue) {
                    return num.floatValue();
                } else {
                    return num.doubleValue();
                }
            }
        }
        return null;
    }
}
