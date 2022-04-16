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

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Valid {

    private Valid() {}

    public static boolean isConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            return false;
        }
        return ClassUtil.hasZeroArgsConstructor(clazz);
    }

    public static <T> Class<T> ensureConfigClass(final Class<T> clazz) {
        checkNotNull(clazz, "clazz");
        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }
        return clazz;
    }

    public static void ensureInstantiableConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        ensureConfigClass(clazz);
        ensureClassHasZeroArgsConstructor(clazz);
    }

    private static void ensureClassHasZeroArgsConstructor(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        final boolean hasDefaultConstructor = ClassUtil.hasZeroArgsConstructor(clazz);
        if (!hasDefaultConstructor) {
            throw new MissingNoArgsConstructorException(clazz);
        }
    }
}
