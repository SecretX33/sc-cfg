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
package com.github.secretx33.sccfg.serialization.namemapping.mapper;

import com.github.secretx33.sccfg.api.NameStrategy;

import java.util.Locale;

public final class LowercaseHyphenatedMapper extends AbstractNameMapper {

    public LowercaseHyphenatedMapper(final NameStrategy nameStrategy) {
        super(nameStrategy);
    }

    @Override
    String applyNameStrategy(final String name, final NameScheme nameScheme) {
        switch (nameScheme) {
            case CAMEL_CASE:
                return replaceAll(name, "[A-Z]", "-$0").toLowerCase(Locale.US);
            case SNAKE_CASE:
                return name.replace('_', '-');
            case UPPERCASE_SNAKE_CASE:
                return name.toLowerCase(Locale.US).replace('_', '-');
            default:
                throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
        }
    }
}
