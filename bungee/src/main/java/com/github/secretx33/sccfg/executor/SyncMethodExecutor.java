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

import com.github.secretx33.sccfg.config.MethodWrapper;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SyncMethodExecutor extends AbstractMethodExecutor implements SyncExecutor {

    private final Plugin plugin;

    public SyncMethodExecutor(final Plugin plugin, final Logger logger) {
        super(logger);
        this.plugin = checkNotNull(plugin, "plugin");
    }

    @Override
    public void runMethodsSync(final Object instance, final Set<MethodWrapper> tasks) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");
        if (tasks.isEmpty()) return;
        runSync(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper)));
    }

    @Override
    public void runMethodsSyncWithLatch(final Object instance, final Set<MethodWrapper> tasks, final CountDownLatch latch) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");
        checkNotNull(latch, "latch");
        if (tasks.isEmpty()) return;
        runSync(() -> tasks.forEach(wrapper -> runCatching(instance, wrapper, latch)));
    }

    private void runSync(final Runnable task) {
        plugin.getProxy().getScheduler().schedule(plugin, task, 0L, 0L, TimeUnit.MILLISECONDS);
    }
}
