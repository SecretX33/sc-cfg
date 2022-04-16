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

import org.jetbrains.annotations.Nullable;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Enum that maps all supported platforms to a class only present when running that given platform. It also holds
 * what SC-CFG class should be used for that specific platform.<br><br>
 *
 * Order of declarations here matter.
 */
enum PlatformType {
    SPIGOT("org.spigotmc.SpigotConfig", "com.github.secretx33.sccfg.platform.BukkitPlatform"),
    BUNGEE("net.md_5.bungee.config.Configuration", "com.github.secretx33.sccfg.platform.BungeePlatform"),
    STANDALONE(null, "com.github.secretx33.sccfg.platform.StandalonePlatform");

    @Nullable
    private final String testClass;
    private final String platformClass;

    PlatformType(@Nullable final String testClass, final String platformClass) {
        this.testClass = testClass;
        this.platformClass = platformClass;
    }

    public String getPlatformClass() {
        return platformClass;
    }

    static final PlatformType ACTUAL;

    static {
        PlatformType platformType = null;
        for (final PlatformType type : values()) {
            try {
                if (type.testClass != null) {
                    Class.forName(type.testClass);
                }
                platformType = type;
                break;
            } catch (final ClassNotFoundException ignored) {
            }
        }
        ACTUAL = checkNotNull(platformType, "platformType");
    }
}
