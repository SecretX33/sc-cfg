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

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public final class Preconditions {

    private Preconditions() {}

    @Contract("false, _ -> fail")
    public static void checkArgument(boolean expression, final String errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    @Contract("false, _ -> fail")
    public static void checkArgument(boolean expression, final Supplier<String> errorMessage) {
        if (!expression) {
            throw new IllegalArgumentException(errorMessage.get());
        }
    }

    @Contract("false, _ -> fail")
    public static void checkState(boolean expression, final Supplier<String> errorMessage) {
        if (!expression) {
            throw new IllegalStateException(errorMessage.get());
        }
    }

    @Contract("null, _ -> fail; _, _ -> param1")
    public static <T> T checkNotNull(@Nullable final T reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        return reference;
    }

    @Contract("null, _ -> fail; _, _ -> param1")
    public static <T> T checkNotNull(@Nullable final T reference, final Supplier<String> errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage.get());
        }
        return reference;
    }

    @Contract("null, _ -> fail")
    public static String checkNotBlank(@Nullable final String reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " string cannot be null");
        }
        if (reference.isEmpty()) {
            throw new IllegalArgumentException(variableName + " string cannot be empty");
        }
        if (reference.trim().isEmpty()) {
            throw new IllegalArgumentException(variableName + " string cannot be blank");
        }
        return reference;
    }

    @Contract("null, _ -> fail")
    public static String checkNotBlank(@Nullable final String reference, final Supplier<String> errorMessage) {
        if (reference == null) {
            throw new NullPointerException(errorMessage.get());
        }
        if (reference.trim().isEmpty()) {
            throw new IllegalArgumentException(errorMessage.get());
        }
        return reference;
    }

    @Contract("null, _ -> fail")
    public static <T> T[] notContainsNull(@Nullable final T[] reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        for (final T element : reference) {
            if (element == null) {
                throw new NullPointerException(variableName + " array cannot have null items");
            }
        }
        return reference;
    }

    @Contract("null, _ -> fail")
    public static <T> Collection<T> notContainsNull(@Nullable final Collection<T> reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        if (reference.contains(null)) {
            throw new NullPointerException(variableName + " " + reference.getClass().getSimpleName().toLowerCase(Locale.US) + " cannot have null items");
        }
        return reference;
    }

    @Contract("null, _ -> fail")
    public static <T> Set<T> notContainsNull(@Nullable final Set<T> reference, final String variableName) {
        return (Set<T>)notContainsNull((Collection<T>)reference, variableName);
    }

    @Contract("null, _ -> fail")
    public static <T> List<T> notContainsNull(@Nullable final List<T> reference, final String variableName) {
        return (List<T>)notContainsNull((Collection<T>)reference, variableName);
    }

    @Contract("null, _ -> fail")
    public static <K, V> Map<K, V> notContainsNull(@Nullable final Map<K, V> reference, final String variableName) {
        if (reference == null) {
            throw new NullPointerException(variableName + " cannot be null");
        }
        for (final Map.Entry<K, V> entry : reference.entrySet()) {
            if (entry.getKey() == null) {
                throw new NullPointerException(variableName + " map passed as argument cannot have null keys");
            }
            if (entry.getValue() == null) {
                throw new NullPointerException(variableName + " map passed as argument cannot have null values");
            }
        }
        return reference;
    }
}
