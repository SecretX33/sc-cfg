package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class AsyncExecutor extends AbstractMethodExecutor {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public AsyncExecutor(final Logger logger) {
        super.logger = checkNotNull(logger);
    }

    public void delayedRun(final long millis, final Runnable task) {
        checkArgument(millis >= 0L);
        checkNotNull(task, "task cannot be null");

        executor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    public void runMethodsAsync(final Object instance, final Set<MethodWrapper> tasks) {
        checkNotNull(instance, "instance cannot be null");
        checkNotNull(tasks, "tasks cannot be null");

        if (tasks.isEmpty()) return;
        CompletableFuture.runAsync(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    public void runMethodsAsyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        checkNotNull(instance, "instance cannot be null");
        checkNotNull(tasks, "tasks cannot be null");
        checkNotNull(latch, "latch cannot be null");

        if (tasks.isEmpty()) return;
        CompletableFuture.runAsync(() -> tasks.forEach(wrapper ->  runCatching(instance, wrapper, latch)));
    }
}
