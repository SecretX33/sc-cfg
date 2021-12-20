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

import com.github.secretx33.sccfg.config.BaseConfigFactory;
import com.github.secretx33.sccfg.config.ConfigFactory;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.executor.SyncMethodExecutor;
import com.github.secretx33.sccfg.scanner.BaseScanner;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import net.md_5.bungee.api.plugin.Plugin;

import java.lang.reflect.Field;

public final class BungeePlatform implements Platform {

    private final GsonFactory gsonFactory;
    private final ConfigFactory configFactory;

    public BungeePlatform() {
        final Plugin plugin = getProvidingPlugin();
        final Scanner scanner = new BaseScanner(plugin);
        final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
        this.gsonFactory = new GsonFactory(plugin.getLogger(), scanner);
        this.configFactory = new BaseConfigFactory(
                plugin.getLogger(),
                gsonFactory,
                plugin.getDataFolder().toPath(),
                scanner,
                fileWatcher,
                new SyncMethodExecutor(plugin, plugin.getLogger())
        );
    }

    private Plugin getProvidingPlugin() {
        final Class<?> pluginClassLoader = BungeePlatform.class.getClassLoader().getClass();
        if (!pluginClassLoader.getCanonicalName().equals("net.md_5.bungee.api.plugin.PluginClassloader")) {
            throw new ConfigException("Unable to get providing plugin");
        }
        final Field pluginField;
        try {
            pluginField = pluginClassLoader.getDeclaredField("plugin");
        } catch (final NoSuchFieldException e) {
            throw new ConfigReflectiveOperationException("Unable to find 'plugin' field inside class '" + pluginClassLoader.getCanonicalName() + "'", e);
        }
        pluginField.setAccessible(true);
        try {
            return (Plugin) pluginField.get(pluginClassLoader);
        } catch (final IllegalAccessException e) {
            throw new ConfigReflectiveOperationException("Unable to get 'plugin' field from class '" + pluginClassLoader.getCanonicalName() + "'", e);
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
