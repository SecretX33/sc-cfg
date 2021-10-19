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

import com.github.secretx33.sccfg.config.AbstractConfig;
import com.github.secretx33.sccfg.config.BukkitConfigFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Config extends AbstractConfig {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);

    static {
        scannerFactory =  new BukkitScannerFactory(plugin);
        fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
        gsonFactory = new GsonFactory(plugin.getLogger(), scannerFactory.getScanner());
        configFactory = new BukkitConfigFactory(
                plugin,
                plugin.getDataFolder().toPath(),
                scannerFactory.getScanner(),
                fileWatcher,
                new SerializerFactory(plugin.getLogger(), gsonFactory));
    }

    private Config() {}
}
