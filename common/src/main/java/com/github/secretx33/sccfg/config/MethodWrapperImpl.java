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
package com.github.secretx33.sccfg.config;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class MethodWrapperImpl implements MethodWrapper {

    private final Method method;
    private final boolean async;

    public MethodWrapperImpl(final Method method, final boolean async) {
        checkArgument(method.isAccessible(), () -> "method needs to be accessible before it can be wrapped, but '" + method.getName() + "' from class '" + method.getDeclaringClass().getCanonicalName() + "' was not set accessible");
        this.method = checkNotNull(method, "method");
        this.async = async;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public boolean isAsync() {
        return async;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MethodWrapperImpl that = (MethodWrapperImpl) o;
        return async == that.async && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, async);
    }

    @Override
    public String toString() {
        return "MethodWrapper{" +
                "method=" + method +
                ", async=" + async +
                '}';
    }
}
