package com.github.secretx33.sccfg.scanner;

public class BukkitScannerFactory implements ScannerFactory {

    @Override
    public Scanner getScanner() {
        return new BukkitScanner();
    }
}
