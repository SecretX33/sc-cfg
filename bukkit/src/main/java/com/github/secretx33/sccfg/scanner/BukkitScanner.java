package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.util.Sets;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitScanner extends BaseScanner {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(BukkitScanner.class);

    public BukkitScanner() {
        super.extraClassLoaders = Sets.immutableOf(plugin.getClass().getClassLoader());
    }
}
