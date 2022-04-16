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
package com.github.secretx33.sccfg.config;

import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.checkState;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

/**
 * Represents an entry of a config instance, and holds relevant data of that particular field, like
 * where it should be stored at (relative to the root of the file), what name should be used, etc.
 */
public final class PropertyWrapperImpl implements PropertyWrapper {

    /**
     * The instance of a config class.
     */
    private final Object instance;

    /**
     * A field belonging to {@link PropertyWrapperImpl#instance} class (or any of its parents) which will be
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

    /**
     * All comments lines that should be placed above the property's entry in the file joined by {@code \n}. If
     * there are no comments, this will be {@code null}.
     */
    @Nullable
    private final String comment;

    public PropertyWrapperImpl(final Object instance, final Field field, final String nameOnFile, final String path, final String[] comments) {
        this.instance = checkNotNull(instance, "instance");
        this.field = checkNotNull(field, "field");
        this.nameOnFile = checkNotBlank(nameOnFile, "nameOnFile");
        this.path = checkNotNull(path, "path");
        this.comment = notContainsNull(comments, "comments").length > 0 ? String.join("\n", comments) : null;
        checkState(field.getDeclaringClass().isAssignableFrom(instance.getClass()), () -> "field passed as argument belongs to class '" + field.getDeclaringClass().getName() + "', but instance passed as argument is an instance of '" + instance.getClass().getName() + "' which does not inherit from class '" + field.getDeclaringClass().getName() + "'!");
        checkState(field.isAccessible(), () -> "field must be made accessible in order to be wrapped into a PropertyWrapper (since sc-cfg library relies on accessing it), but field '" + field.getName() + "' from class '" + field.getDeclaringClass().getName() + "' was not!");
    }

    @Override
    public String getName() {
        return field.getName();
    }

    @Nullable
    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public boolean hasComment() {
        return comment != null;
    }

    @Override
    public String getNameOnFile() {
        return nameOnFile;
    }

    @Override
    public String getPathOnFile() {
        return path;
    }

    @Override
    public String getFullPathOnFile() {
        return isAtRoot() ? getNameOnFile() : (getPathOnFile() + "." + getNameOnFile());
    }

    @Override
    public Class<?> getType() {
        return field.getType();
    }

    @Override
    public Type getGenericType() {
        return field.getGenericType();
    }

    @Override
    public Class<?> getOwnerClass() {
        return instance.getClass();
    }

    @Override
    public boolean isAtRoot() {
        return path.isEmpty();
    }

    @Override
    public Object get() {
        try {
            return field.get(instance);
        } catch (final IllegalAccessException e) {
            throw new ConfigReflectiveOperationException(e);  // this should never be thrown
        }
    }

    @Override
    public void set(final Object value) throws IllegalArgumentException {
        try {
            field.set(instance, value);
        } catch (final IllegalAccessException e) {
            throw new ConfigReflectiveOperationException(e);  // this should never be thrown
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final PropertyWrapperImpl that = (PropertyWrapperImpl) o;
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
        return "PropertyWrapper{" +
                "instance=" + instance +
                ", field=" + field +
                ", nameOnFile='" + nameOnFile + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
