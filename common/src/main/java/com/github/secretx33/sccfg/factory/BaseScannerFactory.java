package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.scanner.BaseScanner;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.scanner.ScannerFactory;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class BaseScannerFactory implements ScannerFactory {

    private final Scanner scanner;

    public BaseScannerFactory(final String basePackage) {
        checkNotNull(basePackage, "basePackage");
        this.scanner = new BaseScanner(basePackage);
    }

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}
