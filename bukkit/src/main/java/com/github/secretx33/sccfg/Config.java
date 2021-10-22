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
package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.config.BukkitConfigFactory;
import com.github.secretx33.sccfg.config.ConfigFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.scanner.ScannerFactory;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final ScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
    private static final GsonFactory gsonFactory = new GsonFactory(plugin.getLogger(), scannerFactory.getScanner());
    private static final ConfigFactory configFactory = new BukkitConfigFactory(
            plugin,
            plugin.getDataFolder().toPath(),
            scannerFactory.getScanner(),
            fileWatcher,
            new SerializerFactory(plugin.getLogger(), gsonFactory));

    private Config() {}

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
        checkNotNull(type, "type");
        checkNotNull(typeAdapter, "typeAdapter");
        gsonFactory.addTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        checkNotNull(typeAdapters, "typeAdapters");
        gsonFactory.addTypeAdapters(typeAdapters);
    }
}
