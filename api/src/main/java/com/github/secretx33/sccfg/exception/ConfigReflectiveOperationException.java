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
 * Is the same as {@link ConfigInternalErrorException}, but implies that the internal error happened
 * because of a reflection exception. It's important to be able to easily differentiate between an
 * issue related to reflection, and issues caused by other things.
 */
public final class ConfigReflectiveOperationException extends ConfigInternalErrorException {

    public ConfigReflectiveOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigReflectiveOperationException(final Throwable cause) {
        super(cause);
    }
}
