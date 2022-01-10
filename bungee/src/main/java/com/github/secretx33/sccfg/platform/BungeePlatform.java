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
package com.github.secretx33.sccfg.platform;

import com.github.secretx33.sccfg.config.ConfigFactoryImpl;
import com.github.secretx33.sccfg.config.ConfigFactory;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.executor.SyncMethodExecutor;
import com.github.secretx33.sccfg.scanner.ScannerImpl;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactoryImpl;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
final class BungeePlatform implements Platform {

    private final GsonFactory gsonFactory;
    private final ConfigFactory configFactory;

    public BungeePlatform() {
        final Plugin plugin = getProvidingPlugin();
        final Scanner scanner = new ScannerImpl(plugin);
        final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
        this.gsonFactory = new GsonFactoryImpl(plugin.getLogger(), scanner);
        this.configFactory = new ConfigFactoryImpl(
                plugin.getLogger(),
                gsonFactory,
                plugin.getDataFolder().toPath(),
                scanner,
                fileWatcher,
                new SyncMethodExecutor(plugin, plugin.getLogger())
        );
    }

    private Plugin getProvidingPlugin() {
        final Object pluginClassLoader = BungeePlatform.class.getClassLoader();
        final Class<?> pluginClassLoaderClass = pluginClassLoader.getClass();
        if (!pluginClassLoaderClass.getCanonicalName().equals("net.md_5.bungee.api.plugin.PluginClassloader")) {
            throw new ConfigException(String.format("Unable to get providing plugin because the classloader '%s' is not a PluginClassloader", pluginClassLoaderClass.getCanonicalName()));
        }
        try {
            final Field pluginField = pluginClassLoaderClass.getDeclaredField("plugin");
            pluginField.setAccessible(true);
            return (Plugin) pluginField.get(pluginClassLoader);
        } catch (final Exception e) {
            throw new ConfigReflectiveOperationException("Unable to find or get 'plugin' field inside class '" + pluginClassLoaderClass.getCanonicalName() + "'", e);
        }
    }

    @Override
    public ConfigFactory getConfigFactory() {
        return configFactory;
    }

    @Override
    public GsonFactory getGsonFactory() {
        return gsonFactory;
    }
}
