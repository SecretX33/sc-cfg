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

import com.github.secretx33.sccfg.api.FileType;

/**
 * Thrown when the consumer forget to add a serializer dependency. It may not seem too important to have
 * a dedicated exception for this, but it actually is!<br><br>
 *
 * Exceptions with descriptive names help the user quickly catch and fix any mistake, and also help us to
 * help them, since the exception name alone is enough for us to know exactly what's wrong with their
 * project.
 */
public final class MissingSerializerDependency extends ConfigException {
    public MissingSerializerDependency(final FileType fileType, final Throwable cause) {
        super("You forgot to add sc-cfg " + fileType + " serializer to your project, sc-cfg will not be able to serialize your config class of type " + fileType + " unless you add the required serializer.", cause);
    }
}
