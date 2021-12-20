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

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class AbstractMethodExecutor {

    protected Logger logger;

    public void runCatching(final Object instance, final MethodWrapper wrapper) {
        final Method method = wrapper.getMethod();
        try {
            method.invoke(instance);
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown while executing method '" + method.getName() + "' of class " + method.getDeclaringClass().getCanonicalName(), e);
        }
    }

    public void runCatching(final Object instance, final MethodWrapper wrapper, final CountDownLatch latch) {
        final Method method = wrapper.getMethod();
        try {
            method.invoke(instance);
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown while executing method '" + method.getName() + "' of class " + method.getDeclaringClass().getCanonicalName(), e);
        } finally {
            latch.countDown();
        }
    }

    public Runnable runnerCatching(final Object instance, final MethodWrapper wrapper, final CountDownLatch latch) {
        return () -> runCatching(instance, wrapper, latch);
    }
}
