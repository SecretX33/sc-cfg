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

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public final class Preconditions {

    private Preconditions() {}

    public static void checkArgument(boolean expression, final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void checkArgument(boolean expression, final Supplier<String> errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    public static <T> T checkNotNull(@Nullable final T reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        return reference;
    }

    public static <T> T checkNotNull(@Nullable final T reference, final Supplier<String> errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage.get());
        }
        return reference;
    }

    public static <K, V> Map<K, V> notContainsNull(@Nullable final Map<K, V> reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        reference.forEach((key, value) -> {
            if (key == null) {
                throw new NullPointerException(variableName + " passed as argument cannot hold null keys");
            }
            if (value == null) {
                throw new NullPointerException(variableName + " passed as argument cannot hold null values");
            }
        });
        return reference;
    }
}
