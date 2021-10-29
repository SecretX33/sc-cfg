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
package com.github.secretx33.sccfg.serialization.namemapping;

import com.github.secretx33.sccfg.api.Naming;

/**
 * Applies the transformation represented by a {@link Naming} on a string.
 */
public interface NameMapper {

    /**
     * Applies the necessary steps to transform a {@code string} into a string that will fit the
     * specified {@link Naming} pattern.
     *
     * @param string the string to be transformed
     * @return a string that fits the specified {@link Naming} pattern
     */
    String applyStrategy(final String string);
}
