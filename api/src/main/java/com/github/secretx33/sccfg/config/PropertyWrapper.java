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

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

/**
 * Represents a single value within a configuration instance.
 */
public interface PropertyWrapper {

    /**
     * Get the name of this entry, which is its "java name".
     *
     * @return the name of the field, which is also the name of the entry
     */
    String getName();

    /**
     * Get the comment associated with this entry, all in one line, with each line separated by a
     * newline character.
     *
     * @return the comment associated with this entry, or {@code null} if no comment was set
     */
    @Nullable
    String getComment();

    /**
     * Test if this config entry has a comment.
     *
     * @return true if this config entry has a comment, false otherwise
     */
    boolean hasComment();

    /**
     * Get what name this field should name on the file, doesn't include the path, this is only the
     * entry name.
     *
     * @return the name that this entry should have on the file
     */
    String getNameOnFile();

    /**
     * Returns the path of this entry relative to the root of the file. Empty means that this entry
     * should be placed on root of the config file.
     *
     * @return the path of this entry relative to the root of the file.
     */
    String getPathOnFile();

    /**
     * Return the "full path" of this entry, e.g. if the {@link PropertyWrapper#getPathOnFile} is "general" and
     * the {@link PropertyWrapper#getNameOnFile} is "my-entry", then the return of this method will be
     * "general.my-entry".
     *
     * @return the "full path" of this entry, or only the {@code nameOnFile} if the config
     * should be placed at root of the file
     */
    String getFullPathOnFile();

    /**
     * Get the class of the field.
     *
     * @return the class of the field
     */
    Class<?> getType();

    /**
     * Get the full {@link Type} of this entry, which include any generics used.
     *
     * @return the {@code Type} of this entry
     */
    Type getGenericType();

    /**
     * Get the config class.
     *
     * @return the config class that for which this field belongs (it might not be exactly the owner
     * of the field, it can also be a field from a parent class).
     */
    Class<?> getOwnerClass();

    /**
     * If this entry is stored at "root" level on the config file.
     *
     * @return true if this config is stored at "root" level on the config file
     */
    boolean isAtRoot();

    /**
     * Get the current value of this config entry.
     *
     * @return the current value of this config entry
     */
    Object get();

    /**
     * Attempt to set a value on this entry, throwing {@code IllegalAccessException} if value is not
     * compatible with the {@code field} type.
     *
     * @param value the value that should be set on this config entry
     * @throws IllegalArgumentException if the {@code value} provided type is not compatible with the
     *                                  {@link PropertyWrapper#getType()} type
     */
    void set(Object value) throws IllegalArgumentException;
}
