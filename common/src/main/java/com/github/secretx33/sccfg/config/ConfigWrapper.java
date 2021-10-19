package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.api.NameStrategy;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.serialization.namemapping.NameMap;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
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

    public ConfigWrapper(
            final T instance,
            final Configuration configAnnotation,
            final Path destination,
            final Map<String, Object> defaults,
            final NameMap nameMap,
            final Set<Field> configFields,
            final Set<MethodWrapper> runBeforeReload,
            final Set<MethodWrapper> runAfterReload
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

    private Set<MethodWrapper> filterAsync(final Set<MethodWrapper> method) {
        return method.stream().filter(MethodWrapper::isAsync)
            .collect(Collectors.toSet());
    }

    private Set<MethodWrapper> filterSync(final Set<MethodWrapper> method) {
        return method.stream().filter(wrapper -> !wrapper.isAsync())
            .collect(Collectors.toSet());
    }
}
