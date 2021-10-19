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
