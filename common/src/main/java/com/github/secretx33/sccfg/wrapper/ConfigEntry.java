package com.github.secretx33.sccfg.wrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Represents an entry of a config instance, and holds relevant data of that particular field, like
 * where it should be stored at (relative to the root of the file), what name should be used, etc.
 */
public class ConfigEntry {

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
        checkArgument(field.getDeclaringClass().isAssignableFrom(instance.getClass()), () -> "field passed as argument belongs to class '" + field.getDeclaringClass().getName() + "', but instance passed as argument is an instance of '" + instance.getClass().getName() + "' which does not inherit from " + field.getDeclaringClass().getName() + " class!");
    }

    public String getName() {
        return field.getName();
    }

    public String getNameOnFile() {
        return nameOnFile;
    }

    public Type getType() {
        return field.getGenericType();
    }

    public String getPath() {
        return path;
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
     * Attempt to set a value on this entry, throwing {@code IllegalAccessException} if value is not
     * compatible with the {@code field} type.
     *
     * @param value the value that should be set on this particular entry
     * @throws IllegalAccessException if the {@code value} provided type is not compatible with the
     * {@code field} type
     */
    public void set(final Object value) throws IllegalAccessException {
        field.set(instance, checkNotNull(value, "value"));
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
