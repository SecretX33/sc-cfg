package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.factory.ScannerFactory;
import org.bukkit.plugin.Plugin;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class BukkitScannerFactory implements ScannerFactory {

    private final Scanner scanner;

    public BukkitScannerFactory(final Plugin plugin) {
        checkNotNull(plugin, "plugin cannot be null");
        this.scanner = new BukkitScanner(plugin);
    }

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}
