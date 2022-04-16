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
package com.github.secretx33.sccfg.util;

import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ClassUtil {

    private ClassUtil() {}

    public static <T> Constructor<T> zeroArgsConstructor(final Class<T> clazz) {
        Constructor<T> constructor = zeroArgsConstructorOrNull(clazz);
        if (constructor == null) {
            throw new MissingNoArgsConstructorException(clazz);
        }
        return constructor;
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> zeroArgsConstructorOrNull(final Class<T> clazz) {
        checkNotNull(clazz, "clazz");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                constructor.setAccessible(true);
                return (Constructor<T>) constructor;
            }
        }
        return null;
    }

    public static boolean hasZeroArgsConstructor(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                return true;
            }
        }
        return false;
    }
}
