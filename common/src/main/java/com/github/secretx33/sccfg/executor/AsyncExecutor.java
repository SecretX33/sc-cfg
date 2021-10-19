package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

public interface AsyncExecutor {

    void delayedRun(long millis, Runnable task);

    void runMethodsAsync(final Object instance, Set<MethodWrapper> tasks);

    void runMethodsAsyncWithLatch(Object instance, Set<MethodWrapper> tasks, CountDownLatch latch);
}
