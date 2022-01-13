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

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.api.Naming;
import com.github.secretx33.sccfg.api.annotation.AfterReload;
import com.github.secretx33.sccfg.api.annotation.BeforeReload;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.PathComment;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Map;
import java.util.Set;

/**
 * Represents a wrapper around an instance of a configuration class, which holds essential data for its
 * management, while also providing convenience methods to extract data about its config instance and
 * class.
 *
 * @param <T> the configuration class
 */
public interface ConfigWrapper<T> {

    /**
     * Get the instance of the configuration class.
     */
    T getInstance();

    /**
     * Get the {@code Configuration} annotation present on the configuration class.
     */
    Configuration getConfigAnnotation();

    /**
     * Gets the header of the config file, all in one line, with each line separated by a newline character.
     * <br>If {@link Configuration#header()} was not specified, returns {@code null} instead.
     *
     * @return the header of the config file, or {@code null} if not specified
     */
    @Nullable
    String getHeader();

    /**
     * Get the {@code Path} of the configuration file.
     */
    Path getDestination();

    /**
     * Gets what type of file this configuration class is set to be. This determines what extension and what
     * serializer is going to be used when reading from and writing to the file.
     */
    FileType getFileType();

    /**
     * Get what kind of name strategy this configuration class is configured to use.
     */
    Naming getNameStrategy();

    /**
     * Get the default values of all entries present in the configuration class.
     */
    Map<String, Object> getDefaults();

    /**
     * Get all property entries of the configuration instance.
     */
    Set<PropertyWrapper> getProperties();

    /**
     * Return all methods of the configuration class which are annotated with {@link BeforeReload}.
     */
    Set<MethodWrapper> getRunBeforeReloadMethods();

    /**
     * Return only methods of the configuration class which are annotated with {@link BeforeReload} and
     * have {@link BeforeReload#async() BeforeReload.async} set to {@code true}.
     */
    Set<MethodWrapper> getRunBeforeReloadAsyncMethods();

    /**
     * Return only methods of the configuration class which are annotated with {@link BeforeReload} and
     * have {@link BeforeReload#async() BeforeReload.async} set to {@code false}.
     */
    Set<MethodWrapper> getRunBeforeReloadSyncMethods();

    /**
     * Return all methods of the configuration class which are annotated with {@link AfterReload}.
     */
    Set<MethodWrapper> getRunAfterReloadMethods();

    /**
     * Return only methods of the configuration class which are annotated with {@link AfterReload} and
     * have {@link AfterReload#async() AfterReload.async} set to {@code true}.
     */
    Set<MethodWrapper> getRunAfterReloadAsyncMethods();

    /**
     * Return only methods of the configuration class which are annotated with {@link AfterReload} and
     * have {@link AfterReload#async() AfterReload.async} set to {@code false}.
     */
    Set<MethodWrapper> getRunAfterReloadSyncMethods();

    /**
     * Get a map holding the {@link PropertyWrapper#getFullPathOnFile()} as key (for properties),
     * or the {@link PathComment#path()} (for paths), and the comments of that key as value (each line is an entry
     * of the array).<br><br>
     */
    Map<String, String[]> getComments();

    /**
     * Used to prevent trigger of reload methods when saving the config to the disk.
     */
    void registerFileModification();
}
