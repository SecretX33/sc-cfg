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
package com.github.secretx33.sccfg.platform;

import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;
import com.github.secretx33.sccfg.exception.WrongPlatformModuleException;

import java.lang.reflect.Constructor;

public final class PlatformProvider {

    /**
     * Get a new instance of the appropriate platform module through reflection.
     */
    public static Platform getPlatform() {
        final PlatformType platform = PlatformType.ACTUAL;
        try {
            final Constructor<?> constructor = Class.forName(platform.getPlatformClass()).getDeclaredConstructor();
            constructor.setAccessible(true);
            return (Platform) constructor.newInstance();
        } catch (final ClassNotFoundException e) {
            throw new WrongPlatformModuleException("You are using the wrong module of SC-CFG for your current platform, please use the module that provides '" + platform.getPlatformClass() + "'!", e);
        } catch (final Exception e) {
            throw new ConfigInternalErrorException("Could not get Platform because sc-cfg could not instantiate class '" + platform.getPlatformClass() + "', please report this!", e);
        }
    }
}
