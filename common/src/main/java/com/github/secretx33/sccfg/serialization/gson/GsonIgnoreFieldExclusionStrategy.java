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
package com.github.secretx33.sccfg.serialization.gson;

import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class GsonIgnoreFieldExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipField(final FieldAttributes fa) {
        checkNotNull(fa, "fa");
        return fa.getAnnotation(IgnoreField.class) != null;
    }

    @Override
    public boolean shouldSkipClass(final Class<?> clazz) {
        return false;
    }
}
