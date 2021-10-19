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
package com.github.secretx33.sccfg.util;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Valid {

    private Valid() {}

    public static boolean isConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            return false;
        }

        final boolean hasDefaultConstructor = Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0);
        if (!hasDefaultConstructor) {
            return false;
        }
        return true;
    }

    public static void validateConfigClass(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        final Configuration annotation = clazz.getDeclaredAnnotation(Configuration.class);
        if (annotation == null) {
            throw new MissingConfigAnnotationException(clazz);
        }

        final boolean hasDefaultConstructor = Arrays.stream(clazz.getDeclaredConstructors()).anyMatch(c -> c.getParameterCount() == 0);
        if (!hasDefaultConstructor) {
            throw new MissingNoArgsConstructorException(clazz);
        }
    }

    public static void validateConfigClasses(final Class<?>... classes) {
        checkNotNull(classes, "classes");
        Arrays.stream(classes).forEach(Valid::validateConfigClass);
    }

    public static void validateConfigClasses(final Object... instances) {
        checkNotNull(instances, "instances");
        Arrays.stream(instances).map(Object::getClass).forEach(Valid::validateConfigClass);
    }

    private <T> Constructor<?> getDefaultConstructor(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.getParameterCount() == 0)
                .findAny()
                .orElseThrow(() -> new MissingNoArgsConstructorException(clazz));
    }
}
