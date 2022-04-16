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
 * Thrown when the consumer tries to use a configuration which was not initialized or registered yet.
 * <br><br>To fix this issue, you have to either get the instance of your config (which makes sc-cfg
 * initialize it), or register an existing instance of your config class, so sc-cfg will be aware of
 * it.
 */
public final class ConfigNotInitializedException extends ConfigException {

    public ConfigNotInitializedException(final Class<?> clazz) {
        super("Instance of class " + clazz.getName() + " passed as argument is not an instance of an initialized config.");
    }
}
