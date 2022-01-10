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
 * Joins {@link Name}, {@link Path} and {@link Comment} annotations for convenience.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NamedPath {

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
    String path();

    /**
     * Set the passed value as name of this config property. Cannot be empty or blank, otherwise will
     * throw {@code IllegalArgumentException}.
     *
     * @return the name set of this config property
     */
    String name();

    /**
     * Comments to be placed in the config file, each line should be put in a position inside the array.
     * While they can be anything, they're usually used for explaining what the property is for, or what
     * it represents in your code.<br><br>
     *
     * Keep in mind that this won't be applied to the config file if the underlying file type does not
     * support comments.<br><br>
     *
     * @return the comment block to be placed in the config file, right before the property
     */
    String[] comment() default {};
}
