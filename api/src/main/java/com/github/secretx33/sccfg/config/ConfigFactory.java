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
package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigInstanceOverrideException;
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;

/**
 * Represents the factory responsible for creating, registering and holding all configuration instances.
 */
public interface ConfigFactory {

    /**
     * Gets the config wrapper of that class, creating it if necessary.
     *
     * @param configClass the config class
     * @param <T> the type of the config class
     * @return a config wrapper containing the config instance, and a bunch of extra information
     * about it.
     * @throws MissingConfigAnnotationException if {@code configClass} is not annotated with
     * {@link Configuration}
     * @throws MissingNoArgsConstructorException if {@code configClass} doesn't have a registered
     * instance yet, and doesn't have a no-args constructor
     */
    <T> ConfigWrapper<T> getWrapper(Class<T> configClass);

    /**
     * Register an instance of a config class.
     *
     * @param instance the config instance
     * @throws MissingConfigAnnotationException if class of {@code instance} is not annotated with
     * {@link Configuration}
     * @throws ConfigInstanceOverrideException if class of {@code instance} already got an instance associated
     * with it
     */
    void registerInstance(Object instance);

    /**
     * Persist (save) the instance associated with the {@code configClass} to the disk.
     *
     * @param configClass the config class
     * @throws MissingConfigAnnotationException if {@code configClass} is not annotated with
     * {@link Configuration}
     * @throws ConfigNotInitializedException if {@code configClass} was not initialized or
     * registered yet
     * @throws ConfigSerializationException if serializer could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    void saveInstance(Class<?> configClass);

    /**
     * Save the default values of this config class to the disk.
     *
     * @param configClass the config class
     * @param overrideIfExists if true, the config file will be overwritten if it exists, else it won't
     * be touched
     * @param reloadAfterwards if config instance should be reloaded to reflect the new, default values
     * that were saved to the disk
     * @return true if the file was saved to the disk, false if the file already existed or some exception has occurred
     */
    boolean saveDefaults(final Class<?> configClass, final boolean overrideIfExists, final boolean reloadAfterwards);
}
