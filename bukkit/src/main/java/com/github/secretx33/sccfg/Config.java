package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.factory.ConfigFactory;
import com.github.secretx33.sccfg.scanner.BukkitScanner;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(BukkitScanner.class);
    private static final ConfigFactory factory = new ConfigFactory(plugin.getDataFolder().toPath(), new BukkitScanner());

    public static <T> T get(final Class<T> configClass) {
        checkNotNull(configClass, "configClass cannot be null");
        return factory.getWrapper(configClass).getInstance();
    }
}
