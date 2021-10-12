package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.Set;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BukkitConfigFactory extends BaseConfigFactory {

    private final Plugin plugin;

    public BukkitConfigFactory(Plugin plugin, Path basePath, Scanner scanner, FileWatcher fileWatcher) {
        super(basePath, scanner, fileWatcher);
        this.plugin = checkNotNull(plugin);
    }

    @Override
    protected Consumer<FileWatcherEvent> handleReload(ConfigWrapper<?> configWrapper) {
        return event -> {
            final Object instance = configWrapper.getInstance();
            Set<MethodWrapper> asyncBefore = configWrapper.getRunBeforeReloadAsyncMethods();
            Set<MethodWrapper> syncBefore = configWrapper.getRunBeforeReloadSyncMethods();
            Set<MethodWrapper> syncAfter = configWrapper.getRunAfterReloadSyncMethods();
            Set<MethodWrapper> asyncAfter = configWrapper.getRunAfterReloadAsyncMethods();

            if(!asyncBefore.isEmpty()) {
                Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                    asyncBefore.forEach(m -> {
                        try {
                            m.getMethod().invoke(instance);
                        } catch (ReflectiveOperationException e) {
                            e.printStackTrace();
                        }
                    });
                });
            }


            // TODO handle config reload
            System.out.println("Config reloaded...");
        };
    }
}
