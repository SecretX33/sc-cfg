package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SyncMethodExecutor extends AbstractMethodExecutor implements SyncExecutor {

    private final Plugin plugin;

    public SyncMethodExecutor(final Plugin plugin, final Logger logger) {
        this.plugin = checkNotNull(plugin, "plugin");
        super.logger = checkNotNull(logger, "logger");
    }

    @Override
    public void runMethodsSync(final Object instance, final Set<MethodWrapper> tasks) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");

        if (tasks.isEmpty()) return;
        Bukkit.getScheduler().runTask(plugin, () -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    @Override
    public void runMethodsSyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");
        checkNotNull(latch, "latch");

        if (tasks.isEmpty()) return;
        Bukkit.getScheduler().runTask(plugin, () -> tasks.forEach(wrapper -> runCatching(instance, wrapper, latch)));
    }
}
