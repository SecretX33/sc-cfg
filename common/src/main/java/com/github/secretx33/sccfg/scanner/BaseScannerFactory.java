package com.github.secretx33.sccfg.scanner;

public class BaseScannerFactory implements ScannerFactory {

    @Override
    public Scanner getScanner() {
        return new BaseScanner();
    }
}
