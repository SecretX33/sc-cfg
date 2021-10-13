package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class AsyncExecutor extends AbstractMethodExecutor {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    public AsyncExecutor(final Logger logger) {
        super.logger = checkNotNull(logger);
    }

    public void delayedRun(final long millis, final Runnable task) {
        checkArgument(millis >= 0L);
        executor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    public void runMethodsAsync(final Object instance, final Set<MethodWrapper> tasks) {
        if (tasks.isEmpty()) return;
        executor.execute(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    public void runMethodsAsyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        if (tasks.isEmpty()) return;
        executor.execute(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper, latch)));
    }
}
