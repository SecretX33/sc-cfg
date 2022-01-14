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
import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.LowercaseHyphenatedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.LowercaseUnderlinedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.NoneMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.UppercaseHyphenatedMapper;
import com.github.secretx33.sccfg.serialization.namemapping.mapper.UppercaseUnderlinedMapper;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class NameMapperFactory {

    public NameMapper getMapper(final Naming strategy) {
        checkNotNull(strategy, "strategy");
        switch (strategy) {
            case NONE:
                return new NoneMapper();
            case LOWERCASE_HYPHENATED:
                return new LowercaseHyphenatedMapper(strategy);
            case UPPERCASE_HYPHENATED:
                return new UppercaseHyphenatedMapper(strategy);
            case LOWERCASE_UNDERLINED:
                return new LowercaseUnderlinedMapper(strategy);
            case UPPERCASE_UNDERLINED:
                return new UppercaseUnderlinedMapper(strategy);
            default:
                throw new ConfigInternalErrorException("There is no registered name mapper for name strategy " + strategy + ", please report this!");
        }
    }
}
