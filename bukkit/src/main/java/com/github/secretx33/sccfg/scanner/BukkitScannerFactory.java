package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.factory.ScannerFactory;
import org.bukkit.plugin.Plugin;

public class BukkitScannerFactory implements ScannerFactory {

    private final Scanner scanner;

    public BukkitScannerFactory(final Plugin plugin) {
        this.scanner = new BukkitScanner(plugin);
    }

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}
