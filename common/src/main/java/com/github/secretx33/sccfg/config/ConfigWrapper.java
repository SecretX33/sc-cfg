package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.api.FieldNameStrategy;
import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.api.annotation.Configuration;

import java.nio.file.Path;
import java.util.Set;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class ConfigWrapper<T> {

    private final T instance;
    private final Configuration configAnnotation;
    private final Path destination;
    private final FileType fileType;
    private final FieldNameStrategy nameStrategy;
    private final Set<MethodWrapper> runBeforeReloadMethods;
    private final Set<MethodWrapper> runAfterReloadMethods;

    public ConfigWrapper(
        final T instance,
        final Configuration configAnnotation,
        final Path destination,
        final Set<MethodWrapper> runBeforeReload,
        final Set<MethodWrapper> runAfterReload
    ) {
        this.instance = checkNotNull(instance);
        this.configAnnotation = checkNotNull(configAnnotation);
        this.destination = destination;
        this.fileType = checkNotNull(configAnnotation.type(), "type cannot be null");
        this.nameStrategy = checkNotNull(configAnnotation.nameStrategy(), "nameStrategy cannot be null");
        this.runBeforeReloadMethods = checkNotNull(runBeforeReload);
        this.runAfterReloadMethods = checkNotNull(runAfterReload);
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

    public FieldNameStrategy getNameStrategy() {
        return nameStrategy;
    }

    public Set<MethodWrapper> getRunBeforeReloadMethods() {
        return runBeforeReloadMethods;
    }

    public Set<MethodWrapper> getRunAfterReloadMethods() {
        return runAfterReloadMethods;
    }
}
