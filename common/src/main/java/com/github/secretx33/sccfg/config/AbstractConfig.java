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
package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.scanner.ScannerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public abstract class AbstractConfig {

    protected static ScannerFactory scannerFactory;
    protected static FileWatcher fileWatcher;
    protected static GsonFactory gsonFactory;
    protected static ConfigFactory configFactory;

    public static <T> T getConfig(final Class<T> configClass) {
        checkNotNull(configClass, "configClass");
        return configFactory.getWrapper(configClass).getInstance();
    }

    public static <T> T registerConfig(final T configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.registerInstance(configInstance);
        return configInstance;
    }

    public static void registerConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstances");
        Arrays.stream(configInstances).forEach(configFactory::registerInstance);
    }

    public static void saveConfig(final Object configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.saveInstance(configInstance);
    }

    public static void saveConfig(final Class<?> configClazz) {
        checkNotNull(configClazz, "configClazz");
        configFactory.saveInstance(configClazz);
    }

    public static void saveConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstance");
        Arrays.stream(configInstances).forEach(configFactory::saveInstance);
    }

    public static void registerTypeAdapter(final Type type, final Object typeAdapter) {
        gsonFactory.addTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        gsonFactory.addTypeAdapters(typeAdapters);
    }
}
