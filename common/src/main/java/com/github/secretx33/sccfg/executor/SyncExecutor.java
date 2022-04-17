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
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CountDownLatch;

public interface SyncExecutor {

    /**
     * Execute the given tasks synchronously if the platform allows it, running the tasks async otherwise.
     *
     * @param instance the instance which has all the methods to execute contained on {@code tasks}
     * @param tasks the tasks to execute
     */
    void execute(Object instance, Set<MethodWrapper> tasks);

    /**
     * Execute the given tasks synchronously if the platform allows it, running the tasks async otherwise, counting
     * down the given latch after each task execution.
     *
     * @param instance the instance which has all the methods to execute contained on {@code tasks}
     * @param tasks the tasks to execute
     * @param latch the latch to count down after each task execution
     */
    void execute(Object instance, Set<MethodWrapper> tasks, @Nullable CountDownLatch latch);
}
