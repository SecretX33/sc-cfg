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
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.util.Sets;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

public final class ConfigWrapperImpl<T> implements ConfigWrapper<T> {

    private final T instance;
    private final Configuration configAnnotation;
    @Nullable
    private final String header;
    private final Path destination;
    private final FileType fileType;
    private final Naming nameStrategy;
    private final Map<String, Object> defaults;
    private final Set<PropertyWrapper> properties;
    private final Set<MethodWrapper> runBeforeReloadMethods;
    private final Set<MethodWrapper> runAfterReloadMethods;
    private final FileWatcher.WatchedLocation watchedLocation;

    public ConfigWrapperImpl(
            final T instance,
            final Configuration configAnnotation,
            final Path destination,
            final Map<String, Object> defaults,
            final Set<PropertyWrapper> properties,
            final Set<MethodWrapper> runBeforeReload,
            final Set<MethodWrapper> runAfterReload,
            final FileWatcher.WatchedLocation watchedLocation
    ) {
        this.instance = checkNotNull(instance, "instance");
        this.configAnnotation = checkNotNull(configAnnotation, "configAnnotation");
        this.header = notContainsNull(configAnnotation.header(), "header").length > 0 ? String.join("\n", configAnnotation.header()) : null;
        this.destination = checkNotNull(destination, "destination");
        this.fileType = checkNotNull(configAnnotation.type(), "type");
        this.nameStrategy = checkNotNull(configAnnotation.naming(), "nameStrategy");
        this.defaults = checkNotNull(defaults, "defaults");
        this.properties = notContainsNull(properties, "properties");
        this.runBeforeReloadMethods = notContainsNull(runBeforeReload, "runBeforeReload");
        this.runAfterReloadMethods = notContainsNull(runAfterReload, "runAfterReload");
        this.watchedLocation = checkNotNull(watchedLocation, "watchedLocation");
    }

    @Override
    public T getInstance() {
        return instance;
    }

    @Override
    public Configuration getConfigAnnotation() {
        return configAnnotation;
    }

    @Nullable
    @Override
    public String getHeader() {
        return header;
    }

    @Override
    public Path getDestination() {
        return destination;
    }

    @Override
    public FileType getFileType() {
        return fileType;
    }

    @Override
    public Naming getNameStrategy() {
        return nameStrategy;
    }

    @Override
    public Map<String, Object> getDefaults() {
        return defaults;
    }

    @Override
    public Set<PropertyWrapper> getProperties() {
        return properties;
    }

    @Override
    public Set<MethodWrapper> getRunBeforeReloadMethods() {
        return runBeforeReloadMethods;
    }

    @Override
    public Set<MethodWrapper> getRunBeforeReloadAsyncMethods() {
        return Sets.filter(runBeforeReloadMethods, MethodWrapper::isAsync);
    }

    @Override
    public Set<MethodWrapper> getRunBeforeReloadSyncMethods() {
        return Sets.filter(runBeforeReloadMethods, MethodWrapper::isSync);
    }

    @Override
    public Set<MethodWrapper> getRunAfterReloadMethods() {
        return runAfterReloadMethods;
    }

    @Override
    public Set<MethodWrapper> getRunAfterReloadAsyncMethods() {
        return Sets.filter(runAfterReloadMethods, MethodWrapper::isAsync);
    }

    @Override
    public Set<MethodWrapper> getRunAfterReloadSyncMethods() {
        return Sets.filter(runAfterReloadMethods, MethodWrapper::isSync);
    }

    @Override
    public void registerFileModification() {
        watchedLocation.recordChange(destination);
    }

    @Override
    public Map<String, String[]> getComments() {
        final Map<String, String[]> comments = new LinkedHashMap<>();
        for (final PropertyWrapper entry : getProperties()) {
            final String comment = entry.getComment();
            if (comment == null || !entry.hasComment()) continue;

            final String[] commentLines = comment.split("\\n");
            if (commentLines.length > 0) {
                final String key = entry.getFullPathOnFile();
                comments.put(key, commentLines);
            }
        }
        return comments;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigWrapperImpl<?> that = (ConfigWrapperImpl<?>) o;
        return instance.equals(that.instance)
                && configAnnotation.equals(that.configAnnotation)
                && destination.equals(that.destination)
                && fileType == that.fileType
                && nameStrategy == that.nameStrategy
                && defaults.equals(that.defaults)
                && properties.equals(that.properties)
                && runBeforeReloadMethods.equals(that.runBeforeReloadMethods)
                && runAfterReloadMethods.equals(that.runAfterReloadMethods)
                && watchedLocation.equals(that.watchedLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, configAnnotation, destination, fileType, nameStrategy, defaults, properties, runBeforeReloadMethods, runAfterReloadMethods, watchedLocation);
    }

    @Override
    public String toString() {
        return "ConfigWrapper{" +
                "instance=" + instance +
                ", configAnnotation=" + configAnnotation +
                ", destination=" + destination +
                ", fileType=" + fileType +
                ", nameStrategy=" + nameStrategy +
                ", defaults=" + defaults +
                ", properties=" + properties +
                ", runBeforeReloadMethods=" + runBeforeReloadMethods +
                ", runAfterReloadMethods=" + runAfterReloadMethods +
                ", watchedLocation=" + watchedLocation +
                '}';
    }
}
