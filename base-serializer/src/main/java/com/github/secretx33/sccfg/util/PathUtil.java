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
package com.github.secretx33.sccfg.util;

import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.List;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class PathUtil {

    private PathUtil() {}

    /**
     * Spread a path, using dots as separator for the layers. E.g. given the input {@code some.path.property} should
     * return {@code List["some", "path", "property"]}.
     *
     * @param configPath the path to be spread
     * @return the spread path
     */
    @Contract(pure = true)
    public static List<String> spreadPath(final String configPath) {
        checkNotNull(configPath, "configPath");
        return Arrays.asList(configPath.split("\\."));
    }
}