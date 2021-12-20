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
package com.github.secretx33.sccfg.exception;

/**
 * Thrown when the using the wrong module for the current platform, e.g. using {@code bukkit} module for {@code bungee}
 * platform, or using one of the former for a {@code standalone} platform.
 */
public final class WrongPlatformModuleException extends ConfigException {

    public WrongPlatformModuleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
