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
package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.wrapper.MethodWrapper;

import java.lang.reflect.Field;
import java.util.Set;

public interface Scanner {

    /**
     * Return a set containing all type adapters classes that are <b>within</b> the sccfg
     * classpath.
     */
    Set<Class<?>> getBaseRegisterTypeAdapters();

    /**
     * Return a set containing all type adapters classes that are <b>not</b> in the sccfg
     * classpath.
     */
    Set<Class<?>> getCustomRegisterTypeAdapters();

    /**
     * Return a set containing all methods that should be run before reloading the config instance.
     */
    Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz);

    /**
     * Return a set containing all methods that should be run after reloading the config instance.
     */
    Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz);

    /**
     * Return a set containing all field that should be serialized.
     */
    Set<Field> getConfigurationFields(final Class<?> clazz);

    /**
     * Return a set containing all field that should not be serialized.
     */
    Set<Field> getIgnoredFields(final Class<?> clazz);
}
