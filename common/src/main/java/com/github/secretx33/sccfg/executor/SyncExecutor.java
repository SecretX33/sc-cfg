package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

public interface SyncExecutor {

    void runMethodsSync(Object instance, Set<MethodWrapper> tasks);

    void runMethodsSyncWithLatch(Object instance, Set<MethodWrapper> tasks, CountDownLatch latch);
}
