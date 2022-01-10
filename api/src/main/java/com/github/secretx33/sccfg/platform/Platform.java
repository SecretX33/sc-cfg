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

import com.github.secretx33.sccfg.config.ConfigFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;

/**
 * Interface used to delegate all platform-specific operations to their implementation.
 */
public interface Platform {

    /**
     * Returns the {@link ConfigFactory} for this platform.
     *
     * @return a valid {@code ConfigFactory} for this platform.
     */
    ConfigFactory getConfigFactory();

    /**
     * Gets the {@link GsonFactory} for this platform.
     *
     * @return a valid {@code GsonFactory} for this platform.
     */
    GsonFactory getGsonFactory();
}
