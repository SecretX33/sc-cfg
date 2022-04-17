/*
 * Copyright (C) 2021-2022 SecretX <notyetmidnight@gmail.com>
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
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class SyncMethodExecutor extends AbstractMethodExecutor implements SyncExecutor {

    private final Plugin plugin;

    public SyncMethodExecutor(final Plugin plugin, final Logger logger) {
        super(logger);
        this.plugin = checkNotNull(plugin, "plugin");
    }

    @Override
    public void execute(final Object instance, final Set<MethodWrapper> tasks) {
        execute(instance, tasks, null);
    }

    @Override
    public void execute(final Object instance, final Set<MethodWrapper> tasks, @Nullable final CountDownLatch latch) {
        checkNotNull(instance, "instance");
        checkNotNull(tasks, "tasks");
        checkNotNull(latch, "latch");

        if (tasks.isEmpty()) return;
        runSync(() -> tasks.forEach(wrapper -> execute(instance, wrapper, latch)));
    }

    private void runSync(final Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }
}
