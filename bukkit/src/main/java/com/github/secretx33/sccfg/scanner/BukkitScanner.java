package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.util.Sets;
import org.bukkit.plugin.Plugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BukkitScanner extends BaseScanner {

    public BukkitScanner(final Plugin plugin) {
        checkNotNull(plugin);
        super.extraClassLoaders = Sets.immutableOf(plugin.getClass().getClassLoader());
    }
}
