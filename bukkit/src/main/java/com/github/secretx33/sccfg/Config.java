package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.factory.ConfigFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final BukkitScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final ConfigFactory factory = new ConfigFactory(plugin.getDataFolder().toPath(), scannerFactory.getScanner());

    private Config() {}
    
    public static <T> T get(final Class<T> configClass) {
        checkNotNull(configClass, "configClass cannot be null");
        final ConfigWrapper<T> wrapper = factory.getWrapper(configClass);

        return wrapper.getInstance();
    }
}
