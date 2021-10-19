package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.executor.AsyncMethodExecutor;
import com.github.secretx33.sccfg.executor.SyncMethodExecutor;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;

public final class BukkitConfigFactory extends BaseConfigFactory {

    public BukkitConfigFactory(
        final Plugin plugin,
        final Path basePath,
        final Scanner scanner,
        final FileWatcher fileWatcher,
        final SerializerFactory serializerFactory
    ) {
        super(basePath, scanner, fileWatcher, serializerFactory, new AsyncMethodExecutor(plugin.getLogger()), new SyncMethodExecutor(plugin, plugin.getLogger()));
    }
}
