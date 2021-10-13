package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.scanner.Scanner;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherEvent;
import com.github.secretx33.sccfg.util.AsyncExecutor;
import com.github.secretx33.sccfg.util.SyncExecutor;
import org.bukkit.plugin.Plugin;

import java.nio.file.Path;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class BukkitConfigFactory extends BaseConfigFactory {

    private final AsyncExecutor asyncExecutor;
    private final SyncExecutor syncExecutor;

    public BukkitConfigFactory(
        final Plugin plugin,
        final Path basePath,
        final Scanner scanner,
        final FileWatcher fileWatcher
    ) {
        super(basePath, scanner, fileWatcher);
        checkNotNull(plugin);
        this.asyncExecutor = new AsyncExecutor(plugin.getLogger());
        this.syncExecutor = new SyncExecutor(plugin, plugin.getLogger());
    }

    @Override
    protected Consumer<FileWatcherEvent> handleReload(final ConfigWrapper<?> configWrapper) {
        return event -> handleReloadAsync(configWrapper);
    }

    private void handleReloadAsync(final ConfigWrapper<?> configWrapper) {
        asyncExecutor.delayedRun(500L, () -> {
            final Object instance = configWrapper.getInstance();
            final Set<MethodWrapper> asyncBefore = configWrapper.getRunBeforeReloadAsyncMethods();
            final Set<MethodWrapper> syncBefore = configWrapper.getRunBeforeReloadSyncMethods();
            final Set<MethodWrapper> syncAfter = configWrapper.getRunAfterReloadSyncMethods();
            final Set<MethodWrapper> asyncAfter = configWrapper.getRunAfterReloadAsyncMethods();
            final int runBeforeCount = asyncBefore.size() + syncBefore.size();
            final CountDownLatch latch = new CountDownLatch(runBeforeCount);

            asyncExecutor.runMethodsAsyncWithLatch(instance, asyncBefore, latch);
            syncExecutor.runMethodsSyncWithLatch(instance, syncBefore, latch);

            if(runBeforeCount > 0) {
                try {
                    latch.await(4L, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    return;
                }
            }

            // TODO handle config reload
            System.out.println("Config reloaded...");

            asyncExecutor.runMethodsAsync(instance, asyncAfter);
            syncExecutor.runMethodsSync(instance, syncAfter);
        });
    }
}
