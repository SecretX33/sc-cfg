package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.factory.BukkitConfigFactory;
import com.github.secretx33.sccfg.factory.ConfigFactory;
import com.github.secretx33.sccfg.factory.GsonFactory;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.scanner.ScannerFactory;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final ScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
    private static final GsonFactory gsonFactory = new GsonFactory(plugin.getLogger(), scannerFactory.getScanner());
    private static final ConfigFactory configFactory = new BukkitConfigFactory(
            plugin,
            plugin.getDataFolder().toPath(),
            scannerFactory.getScanner(),
            fileWatcher,
            new SerializerFactory(plugin.getLogger(), gsonFactory));

    private Config() {}

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
        if(configInstances.length == 0) return;
        Arrays.stream(configInstances).forEach(configFactory::registerInstance);
    }

    public static void saveConfig(final Object configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.saveInstance(configInstance);
    }

    public static void saveConfigs(final Object... configInstances) {
        checkNotNull(configInstances, "configInstance");
        if(configInstances.length == 0) return;
        Arrays.stream(configInstances).forEach(configFactory::saveInstance);
    }

    public static void registerTypeAdapter(final Type type, final Object typeAdapter) {
        gsonFactory.addTypeAdapter(type, typeAdapter);
    }

    public static void registerTypeAdaptersByClass(final Map<Class<?>, Object> typeAdapters) {
        gsonFactory.addTypeAdaptersByClass(typeAdapters);
    }

    public static void registerTypeAdaptersByType(final Map<Type, Object> typeAdapters) {
        gsonFactory.addTypeAdaptersByType(typeAdapters);
    }
}
