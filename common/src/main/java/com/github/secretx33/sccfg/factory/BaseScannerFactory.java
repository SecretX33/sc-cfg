package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.scanner.BaseScanner;
import com.github.secretx33.sccfg.scanner.Scanner;

public class BaseScannerFactory implements ScannerFactory {

    private final Scanner scanner = new BaseScanner();

    @Override
    public Scanner getScanner() {
        return scanner;
    }
}
