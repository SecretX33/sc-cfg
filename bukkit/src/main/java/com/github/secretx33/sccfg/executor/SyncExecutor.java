package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SyncExecutor extends AbstractMethodExecutor {

    private final Plugin plugin;

    public SyncExecutor(final Plugin plugin, final Logger logger) {
        this.plugin = checkNotNull(plugin);
        super.logger = checkNotNull(logger);
    }

    public void runMethodsSync(final Object instance, final Set<MethodWrapper> tasks) {
        if (tasks.isEmpty()) return;
        Bukkit.getScheduler().runTask(plugin, () -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    public void runMethodsSyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        if (tasks.isEmpty()) return;
        Bukkit.getScheduler().runTask(plugin, () -> tasks.forEach(wrapper -> runCatching(instance, wrapper, latch)));
    }
}
