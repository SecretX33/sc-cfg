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
package com.github.secretx33.sccfg.serialization.namemapping.mapper;

import com.github.secretx33.sccfg.api.Naming;
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;

import java.util.Locale;

public final class LowercaseUnderlinedMapper extends AbstractNameMapper {

    public LowercaseUnderlinedMapper(Naming nameStrategy) {
        super(nameStrategy);
    }

    @Override
    String applyNameStrategy(final String name, final NameScheme nameScheme) {
        switch (nameScheme) {
            case CAMEL_CASE:
                return replaceAll(name, "[A-Z]", "_$0").toLowerCase(Locale.US);
            case SNAKE_CASE:
                return name;
            case UPPERCASE_SNAKE_CASE:
                return name.toLowerCase(Locale.US);
            default:
                throw new ConfigInternalErrorException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
        }
    }
}
