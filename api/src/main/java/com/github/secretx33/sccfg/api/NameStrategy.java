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
package com.github.secretx33.sccfg.api;

/**
 * Transformations that should be applied to the config field names when serializing them.
 */
public enum NameStrategy {

    /**
     * This option will not perform any transformations when serializing the field to the file,
     * keeping them as is.<br><br>
     *
     * {@code variable} » {@code variable}<br>
     * {@code VARIABLE} » {@code VARIABLE}<br>
     * {@code myVariableName} » {@code myVariableName}<br>
     * {@code MY_VARIABLE_NAME} » {@code MY_VARIABLE_NAME}
     */
    NONE,

    /**
     * This option will attempt to convert the variable name to a lowercased hyphenated style.<br><br>
     *
     * {@code variable} » {@code variable}<br>
     * {@code VARIABLE} » {@code variable}<br>
     * {@code myVariableName} » {@code my-variable-name}<br>
     * {@code MY_VARIABLE_NAME} » {@code my-variable-name}
     */
    LOWERCASE_HYPHENATED,

    /**
     * This option will attempt to convert the variable name to an uppercased hyphenated style.<br><br>
     *
     * {@code variable} » {@code VARIABLE}<br>
     * {@code VARIABLE} » {@code VARIABLE}<br>
     * {@code myVariableName} » {@code MY-VARIABLE-NAME}<br>
     * {@code MY_VARIABLE_NAME} » {@code MY-VARIABLE-NAME}
     */
    UPPERCASE_HYPHENATED,

    /**
     * This option will attempt to convert the variable name to a lowercased underlined style.<br><br>
     *
     * {@code variable} » {@code variable}<br>
     * {@code VARIABLE} » {@code variable}<br>
     * {@code myVariableName} » {@code my_variable_name}<br>
     * {@code MY_VARIABLE_NAME} » {@code my_variable_name}
     */
    LOWERCASE_UNDERLINED,

    /**
     * This option will attempt to convert the variable name to an uppercased underlined style.<br><br>
     *
     * {@code variable} » {@code VARIABLE}<br>
     * {@code VARIABLE} » {@code VARIABLE}<br>
     * {@code myVariableName} » {@code MY_VARIABLE_NAME}<br>
     * {@code MY_VARIABLE_NAME} » {@code MY_VARIABLE_NAME}
     */
    UPPERCASE_UNDERLINED;
}
