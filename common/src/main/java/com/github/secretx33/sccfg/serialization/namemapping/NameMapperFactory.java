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
import com.github.secretx33.sccfg.serialization.namemapping.mapper.LowercaseHyphenatedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.LowercaseUnderlinedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.NoneMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.UppercaseHyphenatedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.UppercaseUnderlinedMapper;

import java.util.EnumMap;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class NameMapperFactory {

    private final Map<Naming, NameMapper> nameMappers = new EnumMap<>(Naming.class);

    public NameMapper getMapper(final Naming strategy) {
        checkNotNull(strategy, "strategy");
        switch (strategy) {
            case NONE:
                return nameMappers.computeIfAbsent(strategy, s -> new NoneMapper());
            case LOWERCASE_HYPHENATED:
                return nameMappers.computeIfAbsent(strategy, LowercaseHyphenatedMapper::new);
            case UPPERCASE_HYPHENATED:
                return nameMappers.computeIfAbsent(strategy, UppercaseHyphenatedMapper::new);
            case LOWERCASE_UNDERLINED:
                return nameMappers.computeIfAbsent(strategy, LowercaseUnderlinedMapper::new);
            case UPPERCASE_UNDERLINED:
                return nameMappers.computeIfAbsent(strategy, UppercaseUnderlinedMapper::new);
            default:
                throw new IllegalStateException("Oops, I don't have a registered name mapper for name strategy " + strategy + ", what a shame!");
        }
    }
}
