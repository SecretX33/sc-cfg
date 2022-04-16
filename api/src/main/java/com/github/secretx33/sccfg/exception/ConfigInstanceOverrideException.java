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
 * Thrown when the consumer tries to register an instance of a config class which is <b>already</b>
 * registered or instantiated. <br><br>
 *
 * The fix for that is either stop trying to register an instance of a class which is already registered,
 * or make sure that you don't call {@code Config.getConfig(SomeClass.class)} until you register the
 * instance of it yourself with {@code Config.registerConfig(SomeClass.class)}.
 */
public final class ConfigInstanceOverrideException extends ConfigException {

    public ConfigInstanceOverrideException(final Class<?> clazz) {
        super("There's already an instance of config " + clazz.getName() + " registered, you cannot override config instances");
    }
}
