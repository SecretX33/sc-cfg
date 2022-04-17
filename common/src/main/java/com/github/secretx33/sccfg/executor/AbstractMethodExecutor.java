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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractMethodExecutor {

    protected final Logger logger;

    public AbstractMethodExecutor(final Logger logger) {
        this.logger = checkNotNull(logger, "logger");
    }

    public void execute(final Object instance, final MethodWrapper wrapper) {
        execute(instance, wrapper, null);
    }

    public void execute(final Object instance, final MethodWrapper wrapper, @Nullable final CountDownLatch latch) {
        final Method method = wrapper.getMethod();
        try {
            method.invoke(instance);
        } catch (final Throwable e) {
            Throwable cause = (e instanceof InvocationTargetException) ? ((InvocationTargetException) e).getTargetException() : e;
            logger.log(Level.SEVERE, "An exception was thrown while executing method '" + method.getName() + "' of class " + method.getDeclaringClass().getCanonicalName(), cause);
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }
}
