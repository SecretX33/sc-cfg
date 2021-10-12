package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.factory.BukkitConfigFactory;
import com.github.secretx33.sccfg.factory.ConfigFactory;
import com.github.secretx33.sccfg.factory.ScannerFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final ScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
    private static final ConfigFactory factory = new BukkitConfigFactory(plugin.getDataFolder().toPath(), scannerFactory.getScanner(), fileWatcher);

    private Config() {}

    public static <T> T get(final Class<T> configClass) {
        checkNotNull(configClass, "configClass cannot be null");
        return factory.getWrapper(configClass).getInstance();
    }
}
