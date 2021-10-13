package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.util.Sets;
import org.bukkit.plugin.Plugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class BukkitScanner extends BaseScanner {

    public BukkitScanner(final Plugin plugin) {
        super(checkNotNull(plugin, "plugin cannot be null").getClass().getPackage().getName(), Sets.immutableOf(plugin.getClass().getClassLoader()));
    }
}
