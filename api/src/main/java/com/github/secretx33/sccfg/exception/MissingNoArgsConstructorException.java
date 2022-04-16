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
package com.github.secretx33.sccfg.exception;

/**
 * Thrown when the consumer calls {@code Config.getConfig(SomeClass.class)} and the {@code SomeClass}
 * doesn't have a "no-args" (zero args) constructor. <br><br>
 *
 * There are two possible fixes for this issue: <br><br>
 * 1. Create a zero args constructor on {@code SomeClass}.<br>
 * 2. Instantiate {@code SomeClass} yourself, and register it later using {@code Config.registerConfig(configInstance)}.
 */
public final class MissingNoArgsConstructorException extends ConfigException {

    public MissingNoArgsConstructorException(final Class<?> clazz) {
        super("Could not create instance of class " + clazz.getCanonicalName() + " because it is missing no args constructor, please add an empty arg constructor to the class");
    }
}
