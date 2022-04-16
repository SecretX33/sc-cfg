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
package com.github.secretx33.sccfg.util;

public final class BooleanWrapper {

    boolean value = false;

    public BooleanWrapper() {
    }

    public BooleanWrapper(final boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void set(final boolean newValue) {
        value = newValue;
    }
}
