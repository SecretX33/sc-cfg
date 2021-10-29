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
package com.github.secretx33.sccfg.wrapper;

import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.checkState;

/**
 * Represents an entry of a config instance, and holds relevant data of that particular field, like
 * where it should be stored at (relative to the root of the file), what name should be used, etc.
 */
public final class ConfigEntry {

    /**
     * The instance of a config class.
     */
    private final Object instance;

    /**
     * A field belonging to {@link ConfigEntry#instance} class (or any of its parents) which will be
     * saved to and read from a configuration file.
     */
    private final Field field;

    /**
     * What name should be used when saving this entry to the file, will never be empty.
     */
    private final String nameOnFile;

    /**
     * Holds the path inside the file where this config entry should be placed at, use dots to separate
     * between layers (on a Map style), empty means "root of the file".
     */
    private final String path;

    public ConfigEntry(final Object instance, final Field field, final String nameOnFile, final String path) {
        this.instance = checkNotNull(instance, "instance");
        this.field = checkNotNull(field, "field");
        this.nameOnFile = checkNotBlank(nameOnFile, "nameOnFile");
        this.path = checkNotNull(path, "path");
        checkState(field.getDeclaringClass().isAssignableFrom(instance.getClass()), () -> "field passed as argument belongs to class '" + field.getDeclaringClass().getName() + "', but instance passed as argument is an instance of '" + instance.getClass().getName() + "' which does not inherit from " + field.getDeclaringClass().getName() + " class!");
        checkState(field.isAccessible(), () -> "field must be made accessible in order to be wrapped into a ConfigEntry (since sc-cfg library relies on accessing it), but field " + field.getName() + " from class " + field.getDeclaringClass().getName() + " was not!");
    }

    public String getName() {
        return field.getName();
    }

    public String getNameOnFile() {
        return nameOnFile;
    }

    /**
     * Return the "full path" of this entry, e.g. if the {@link ConfigEntry#path} is "general" and
     * the {@link ConfigEntry#nameOnFile} is "my-entry", then the return of this method will be
     * "general.my-entry".
     *
     * @return the "full path" of this entry, or only the {@code nameOnFile} if the config
     * should be placed at root of the file
     */
    public String getPathWithName() {
        return isAtRoot() ? getNameOnFile() : (getPath() + "." + getNameOnFile());
    }

    public Class<?> getType() {
        return field.getType();
    }

    public Type getGenericType() {
        return field.getGenericType();
    }

    public String getPath() {
        return path;
    }

    public Class<?> getOwnerClass() {
        return instance.getClass();
    }

    /**
     * If this entry is stored at "root" level on the config file.
     *
     * @return true if this config is stored at "root" level on the config file
     */
    public boolean isAtRoot() {
        return path.isEmpty();
    }

    /**
     * Get the current value of this config entry.
     *
     * @return the current value of this config entry
     */
    public Object get() {
        try {
            return field.get(instance);
        } catch (final IllegalAccessException e) {
            // this should never be thrown
            throw new ConfigReflectiveOperationException(e);
        }
    }

    /**
     * Attempt to set a value on this entry, throwing {@code IllegalAccessException} if value is not
     * compatible with the {@code field} type.
     *
     * @param value the value that should be set on this config entry
     * @throws IllegalArgumentException if the {@code value} provided type is not compatible with the
     * {@link ConfigEntry#field} type
     */
    public void set(final Object value) throws IllegalArgumentException {
        try {
            field.set(instance, value);
        } catch (final IllegalAccessException e) {
            // this should never be thrown
            throw new ConfigReflectiveOperationException(e);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ConfigEntry that = (ConfigEntry) o;
        return instance.equals(that.instance)
                && field.equals(that.field)
                && nameOnFile.equals(that.nameOnFile)
                && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instance, field, nameOnFile, path);
    }

    @Override
    public String toString() {
        return "ConfigEntry{" +
                "instance=" + instance +
                ", field=" + field +
                ", nameOnFile='" + nameOnFile + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
