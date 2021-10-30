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

import com.github.secretx33.sccfg.api.annotation.Configuration;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Thrown when the consumer passes as argument any class that is not annotated with {@link Configuration @Configuration},
 * which means that they either did forget to add the annotation on their config class, or that they're
 * passing as argument a class that is not meant to be a config class.
 */
public final class MissingConfigAnnotationException extends ConfigException {

    public MissingConfigAnnotationException(final Class<?> clazz) {
        super("Could not create instance of class " + checkNotNull(clazz, "clazz").getName() + " because it is missing @Configuration annotation, please annotate your configuration class with @Configuration");
    }
}
