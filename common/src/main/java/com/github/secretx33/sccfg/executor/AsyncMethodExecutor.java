/*
 * Copyright (C) 2021 SecretX <notyetmidnight@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.wrapper.MethodWrapper;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class AsyncMethodExecutor extends AbstractMethodExecutor implements AsyncExecutor {

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public AsyncMethodExecutor(final Logger logger) {
        super.logger = checkNotNull(logger, "logger");
    }

    @Override
    public void delayedRun(final long millis, final Runnable task) {
        checkArgument(millis >= 0L, () -> "millis: " + millis + " (expected >= 0L)");
        checkNotNull(task, "task");

        executor.schedule(task, millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runMethodsAsync(final Object instance, final Set<MethodWrapper> tasks) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");

        if (tasks.isEmpty()) return;
        CompletableFuture.runAsync(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    @Override
    public void runMethodsAsyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");
        checkNotNull(latch, "latch");

        if (tasks.isEmpty()) return;
        CompletableFuture.runAsync(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper, latch)));
    }
}
