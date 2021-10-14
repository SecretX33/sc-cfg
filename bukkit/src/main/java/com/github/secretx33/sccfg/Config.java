package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.factory.BukkitConfigFactory;
import com.github.secretx33.sccfg.factory.ConfigFactory;
import com.github.secretx33.sccfg.factory.ScannerFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final ScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
    private static final ConfigFactory configFactory = new BukkitConfigFactory(plugin, plugin.getDataFolder().toPath(), scannerFactory.getScanner(), fileWatcher);

    private Config() {}

    public static <T> T getConfig(final Class<T> configClass) {
        checkNotNull(configClass, "configClass cannot be null");
        return configFactory.getWrapper(configClass).getInstance();
    }

    public static <T> T registerConfig(final T configInstance) {
        checkNotNull(configInstance, "configInstance cannot be null");
        configFactory.registerInstance(configInstance);
        return configInstance;
    }

    public static void registerConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstances cannot be null");
        if(configInstances.length == 0) return;
        Arrays.stream(configInstances).forEach(configFactory::registerInstance);
    }
}
