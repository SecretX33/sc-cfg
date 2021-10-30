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
package com.github.secretx33.sccfg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to alter where this property will be stored (relative to the root of the config file).
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Path {

    /**
     * Where this property should be saved relative to the root of the file. Use {@code .} (dot) to
     * separate between layers (like a {@code Map}). Cannot be empty or blank, otherwise will throw
     * {@code IllegalArgumentException}.<br><br>
     *
     * <b>Examples:</b><br><br>
     *
     * {@code ""} » throw {@code IllegalArgumentException}.<br>
     * {@code "  "} » throw {@code IllegalArgumentException}.<br>
     * {@code "general"} » will place this entry under "general" section.<br>
     * {@code "general.options"} » will place this entry under "options" sub-section, which is by itself
     * inside "general" section.
     *
     * @return the path in which this property should be stored at
     */
    String value();
}

