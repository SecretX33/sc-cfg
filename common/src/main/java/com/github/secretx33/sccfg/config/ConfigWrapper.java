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
import com.github.secretx33.sccfg.api.NameStrategy;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;
import com.github.secretx33.sccfg.storage.FileWatcher;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class ConfigWrapper<T> {

    private final T instance;
    private final Configuration configAnnotation;
    private final Path destination;
    private final FileType fileType;
    private final NameStrategy nameStrategy;
    private final Map<String, Object> defaults;
    private final NameMap nameMap;
    private final Set<Field> configFields;
    private final Set<MethodWrapper> runBeforeReloadMethods;
    private final Set<MethodWrapper> runBeforeReloadAsyncMethods;
    private final Set<MethodWrapper> runBeforeReloadSyncMethods;
    private final Set<MethodWrapper> runAfterReloadMethods;
    private final Set<MethodWrapper> runAfterReloadAsyncMethods;
    private final Set<MethodWrapper> runAfterReloadSyncMethods;
    private final FileWatcher.WatchedLocation watchedLocation;

    public ConfigWrapper(
            final T instance,
            final Configuration configAnnotation,
            final Path destination,
            final Map<String, Object> defaults,
            final NameMap nameMap,
            final Set<Field> configFields,
            final Set<MethodWrapper> runBeforeReload,
            final Set<MethodWrapper> runAfterReload,
            final FileWatcher.WatchedLocation watchedLocation
    ) {
        this.instance = checkNotNull(instance, "instance");
        this.configAnnotation = checkNotNull(configAnnotation, "configAnnotation");
        this.destination = checkNotNull(destination, "destination");
        this.fileType = checkNotNull(configAnnotation.type(), "type");
        this.nameStrategy = checkNotNull(configAnnotation.nameStrategy(), "nameStrategy");
        this.defaults = checkNotNull(defaults, "defaults");
        this.nameMap = checkNotNull(nameMap, "nameMap");
        this.configFields = checkNotNull(configFields, "configFields");
        this.runBeforeReloadMethods = checkNotNull(runBeforeReload, "runBeforeReload");
        this.runBeforeReloadAsyncMethods = filterAsync(runBeforeReloadMethods);
        this.runBeforeReloadSyncMethods = filterSync(runBeforeReloadMethods);
        this.runAfterReloadMethods = checkNotNull(runAfterReload, "runAfterReload");
        this.runAfterReloadAsyncMethods = filterAsync(runAfterReloadMethods);
        this.runAfterReloadSyncMethods = filterSync(runAfterReloadMethods);
        this.watchedLocation = checkNotNull(watchedLocation, "watchedLocation");
    }

    public T getInstance() {
        return instance;
    }

    public Configuration getConfigAnnotation() {
        return configAnnotation;
    }

    public Path getDestination() {
        return destination;
    }

    public FileType getFileType() {
        return fileType;
    }

    public NameStrategy getNameStrategy() {
        return nameStrategy;
    }

    public Map<String, Object> getDefaults() {
        return defaults;
    }

    public NameMap getNameMap() {
        return nameMap;
    }

    public Set<Field> getConfigFields() {
        return configFields;
    }

    public Set<MethodWrapper> getRunBeforeReloadMethods() {
        return runBeforeReloadMethods;
    }

    public Set<MethodWrapper> getRunBeforeReloadAsyncMethods() {
        return runBeforeReloadAsyncMethods;
    }

    public Set<MethodWrapper> getRunBeforeReloadSyncMethods() {
        return runBeforeReloadSyncMethods;
    }

    public Set<MethodWrapper> getRunAfterReloadMethods() {
        return runAfterReloadMethods;
    }

    public Set<MethodWrapper> getRunAfterReloadAsyncMethods() {
        return runAfterReloadAsyncMethods;
    }

    public Set<MethodWrapper> getRunAfterReloadSyncMethods() {
        return runAfterReloadSyncMethods;
    }

    /**
     * Used to prevent trigger of reload methods when saving the config to the disk.
     */
    public void registerModification() {
        watchedLocation.recordChange(destination);
    }

    private Set<MethodWrapper> filterAsync(final Set<MethodWrapper> method) {
        return method.stream().filter(MethodWrapper::isAsync)
            .collect(Collectors.toSet());
    }

    private Set<MethodWrapper> filterSync(final Set<MethodWrapper> method) {
        return method.stream().filter(wrapper -> !wrapper.isAsync())
            .collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigWrapper<?> that = (ConfigWrapper<?>) o;
        return instance.equals(that.instance)
                && configAnnotation.equals(that.configAnnotation)
                && destination.equals(that.destination)
                && fileType == that.fileType
                && nameStrategy == that.nameStrategy
                && defaults.equals(that.defaults)
                && nameMap.equals(that.nameMap)
                && configFields.equals(that.configFields)
                && runBeforeReloadMethods.equals(that.runBeforeReloadMethods)
                && runBeforeReloadAsyncMethods.equals(that.runBeforeReloadAsyncMethods)
                && runBeforeReloadSyncMethods.equals(that.runBeforeReloadSyncMethods)
                && runAfterReloadMethods.equals(that.runAfterReloadMethods)
                && runAfterReloadAsyncMethods.equals(that.runAfterReloadAsyncMethods)
                && runAfterReloadSyncMethods.equals(that.runAfterReloadSyncMethods)
                && watchedLocation.equals(that.watchedLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, configAnnotation, destination, fileType, nameStrategy, defaults, nameMap, configFields, runBeforeReloadMethods, runBeforeReloadAsyncMethods, runBeforeReloadSyncMethods, runAfterReloadMethods, runAfterReloadAsyncMethods, runAfterReloadSyncMethods, watchedLocation);
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
                ", nameMap=" + nameMap +
                ", configFields=" + configFields +
                ", runBeforeReloadMethods=" + runBeforeReloadMethods +
                ", runBeforeReloadAsyncMethods=" + runBeforeReloadAsyncMethods +
                ", runBeforeReloadSyncMethods=" + runBeforeReloadSyncMethods +
                ", runAfterReloadMethods=" + runAfterReloadMethods +
                ", runAfterReloadAsyncMethods=" + runAfterReloadAsyncMethods +
                ", runAfterReloadSyncMethods=" + runAfterReloadSyncMethods +
                ", watchedLocation=" + watchedLocation +
                '}';
    }
}
