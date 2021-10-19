package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.scanner.ScannerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public abstract class AbstractConfig {

    protected static ScannerFactory scannerFactory;
    protected static FileWatcher fileWatcher;
    protected static GsonFactory gsonFactory;
    protected static ConfigFactory configFactory;

    public static <T> T getConfig(final Class<T> configClass) {
        checkNotNull(configClass, "configClass");
        return configFactory.getWrapper(configClass).getInstance();
    }

    public static <T> T registerConfig(final T configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.registerInstance(configInstance);
        return configInstance;
    }

    public static void registerConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstances");
        Arrays.stream(configInstances).forEach(configFactory::registerInstance);
    }

    public static void saveConfig(final Object configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.saveInstance(configInstance);
    }

    public static void saveConfig(final Class<?> configClazz) {
        checkNotNull(configClazz, "configClazz");
        configFactory.saveInstance(configClazz);
    }

    public static void saveConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstance");
        Arrays.stream(configInstances).forEach(configFactory::saveInstance);
    }

    public static void registerTypeAdapter(final Type type, final Object typeAdapter) {
        gsonFactory.addTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        gsonFactory.addTypeAdapters(typeAdapters);
    }
}
