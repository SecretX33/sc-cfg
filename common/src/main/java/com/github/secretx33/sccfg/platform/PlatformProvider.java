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
package com.github.secretx33.sccfg.platform;

import com.github.secretx33.sccfg.exception.ConfigInternalErrorException;

import java.lang.reflect.Constructor;

public final class PlatformProvider {

    public static Platform getPlatform() {
        try {
            Class.forName("org.spigotmc.SpigotConfig");
            return getOrThrow("com.github.secretx33.sccfg.platform.BukkitPlatform");
        } catch(final ClassNotFoundException ignored){
        }
        try {
            Class.forName("net.md_5.bungee.config.Configuration");
            return getOrThrow("com.github.secretx33.sccfg.platform.BungeePlatform");
        } catch(final ClassNotFoundException ignored){
        }
        return getOrThrow("com.github.secretx33.sccfg.platform.StandalonePlatform");
    }

    @SuppressWarnings("unchecked")
    private static <T> T getOrThrow(final String className) {
        try {
            final Constructor<?> constructor = Class.forName(className).getDeclaredConstructor();
            constructor.setAccessible(true);
            return (T) constructor.newInstance();
        } catch (ClassCastException | ReflectiveOperationException e) {
            throw new ConfigInternalErrorException("Could not get Platform because sc-cfg could not instantiate class '" + className + "', please report this!", e);
        }
    }
}
