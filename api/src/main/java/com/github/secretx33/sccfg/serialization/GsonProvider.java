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

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Provides a lazy {@link Gson} instance with possibility of registering type adapters at runtime.<br><br>
 *
 * <b>All classes that implement this interface are required to be thread-safe.<b/>
 */
public interface GsonProvider {

    /**
     * Returns a valid Gson instance with all type adapters registered.
     */
    Gson getInstance();

    /**
     * Register a single type adapter for the given type.
     *
     * @param adapterFor The type to register the adapter for.
     * @param typeAdapter The actual adapter instance to register.
     */
    void addTypeAdapter(Type adapterFor, Object typeAdapter);

    /**
     * Register multiple type adapters for the given types.
     *
     * @param typeAdapters The type adapters to register.
     */
    void addTypeAdapters(Map<? extends Type, Object> typeAdapters);
}
