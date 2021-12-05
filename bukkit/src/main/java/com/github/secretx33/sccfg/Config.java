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
package com.github.secretx33.sccfg;

import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.config.BukkitConfigFactory;
import com.github.secretx33.sccfg.config.ConfigFactory;
import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException;
import com.github.secretx33.sccfg.exception.ConfigOverrideException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException;
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException;
import com.github.secretx33.sccfg.scanner.BukkitScannerFactory;
import com.github.secretx33.sccfg.scanner.ScannerFactory;
import com.github.secretx33.sccfg.serialization.SerializerFactory;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.github.secretx33.sccfg.storage.FileWatcher;
import com.github.secretx33.sccfg.storage.FileWatcherProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

/**
 * Class that interfaces with and groups all {@code sc-cfg} features, providing an easy way of using
 * all of them, in other words, all available features of sc-cfg are present here.
 */
public final class Config {

    private static final Plugin plugin = JavaPlugin.getProvidingPlugin(Config.class);
    private static final ScannerFactory scannerFactory = new BukkitScannerFactory(plugin);
    private static final FileWatcher fileWatcher = FileWatcherProvider.get(plugin.getDataFolder().toPath());
    private static final GsonFactory gsonFactory = new GsonFactory(plugin.getLogger(), scannerFactory.getScanner());
    private static final ConfigFactory configFactory = new BukkitConfigFactory(
            plugin,
            plugin.getDataFolder().toPath(),
            scannerFactory.getScanner(),
            fileWatcher,
            new SerializerFactory(plugin.getLogger(), gsonFactory));

    private Config() {}

    /**
     * Gets the instance of the {@code} configClass, instantiating it if it's not initiated yet.
     *
     * @param configClass the config class
     * @param <T> the type of the config class
     * @return the singleton instance of config T
     * @throws MissingConfigAnnotationException if {@code configClass} is not annotated with
     * {@link Configuration}
     * @throws MissingNoArgsConstructorException if {@code configClass} doesn't have an initiated
     * instance registered yet, and doesn't have a no-args constructor
     */
    public static <T> T getConfig(final Class<T> configClass) {
        checkNotNull(configClass, "configClass");
        return configFactory.getWrapper(configClass).getInstance();
    }

    /**
     * Register an instance of a config class.
     *
     * @param configInstance the config instance
     * @param <T> the type of the config class
     * @return the singleton instance of config T
     * @throws MissingConfigAnnotationException if class of {@code configInstance} is not annotated
     * with {@link Configuration}
     * @throws ConfigOverrideException if class of {@code configInstance} already got an instance
     * associated with it
     */
    public static <T> T registerConfig(final T configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.registerInstance(configInstance);
        return configInstance;
    }

    /**
     * Register multiple instances of config classes.
     *
     * @param configInstances the config instances
     * @throws MissingConfigAnnotationException if class of any instance inside {@code configInstances}
     * is not annotated with {@link Configuration}
     * @throws ConfigOverrideException if class of any instance inside {@code configInstances} already
     * got an instance associated with it
     */
    public static void registerConfigs(final Object... configInstances) {
        notContainsNull(configInstances, "configInstances");
        Arrays.stream(configInstances).forEach(configFactory::registerInstance);
    }

    /**
     * Persist (save) a config instance to the disk.
     *
     * @param configInstance the config instance
     * @throws MissingConfigAnnotationException if class of {@code configInstance} is not annotated
     * with {@link Configuration}
     * @throws ConfigNotInitializedException if class of {@code configInstance} was not initialized
     * or registered yet
     * @throws ConfigSerializationException if serializer could not serialize a config entry (that
     * happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    public static void saveConfig(final Object configInstance) {
        checkNotNull(configInstance, "configInstance");
        configFactory.saveInstance(configInstance.getClass());
    }

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
    public static void saveConfig(final Class<?> configClass) {
        checkNotNull(configClass, "configClass");
        configFactory.saveInstance(configClass);
    }

    /**
     * Persist (save) the instance associated with the {@code configClass} to the disk.
     *
     * @param configInstances the config instances
     * @throws MissingConfigAnnotationException if class of any instance inside {@code configInstances}
     * is not annotated with {@link Configuration}
     * @throws ConfigNotInitializedException if class of any instance inside {@code configInstances} was
     * not initialized or registered yet
     * @throws ConfigSerializationException if serializer could not serialize a config entry (that
     * happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    public static void saveConfigs(final Object... configInstances) {
        notContainsNull(configInstances, "configInstance");
        Arrays.stream(configInstances).map(Object::getClass).forEach(configFactory::saveInstance);
    }

    /**
     * See method linked below for documentation.
     *
     * @see Config#saveDefaults(Object, boolean, boolean)
     */
    public static boolean saveDefaults(final Object configInstance) {
        checkNotNull(configInstance, "configInstance");
        return saveDefaults(configInstance.getClass());
    }

    /**
     * See method linked below for documentation.
     *
     * @see Config#saveDefaults(Object, boolean, boolean)
     */
    public static boolean saveDefaults(final Object configInstance, boolean overrideIfExists) {
        checkNotNull(configInstance, "configInstance");
        return saveDefaults(configInstance.getClass(), overrideIfExists);
    }

    /**
     * Save the default values of this config instance to the disk.
     *
     * @param configInstance the config instance
     * @param reloadAfterwards if config instance should be reloaded to reflect the new, default values
     * that were saved to the disk
     * @param overrideIfExists if true, the config file will be overwritten if it exists, else it won't
     * be touched
     * @return true if the file was saved to the disk, false if the file already existed or some exception has occurred
     * @throws ConfigSerializationException if serializer could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigDeserializationException if serializer could not deserialize a config entry
     * back to its java value (that happens when sc-cfg is missing a Type Adapter for that
     * specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    public static boolean saveDefaults(final Object configInstance, boolean overrideIfExists, boolean reloadAfterwards) {
        checkNotNull(configInstance, "configInstance");
        return saveDefaults(configInstance.getClass(), reloadAfterwards, overrideIfExists);
    }

    /**
     * See method linked below for documentation.
     *
     * @see Config#saveDefaults(Class, boolean, boolean)
     */
    public static boolean saveDefaults(final Class<?> configClass) {
        return saveDefaults(configClass, false);
    }

    /**
     * See method linked below for documentation.
     *
     * @see Config#saveDefaults(Class, boolean, boolean)
     */
    public static boolean saveDefaults(final Class<?> configClass, boolean overrideIfExists) {
        return saveDefaults(configClass, overrideIfExists, true);
    }

    /**
     * Save the default values of this config class to the disk.
     *
     * @param configClass the config class
     * @param overrideIfExists if true, the config file will be overwritten if it exists, else it won't
     * be touched
     * @param reloadAfterwards if config instance should be reloaded to reflect the new, default values
     * that were saved to the disk
     * @return true if the file was saved to the disk, false if the file already existed or some exception has occurred
     * @throws ConfigSerializationException if serializer could not serialize a config entry
     * (that happens when sc-cfg is missing a Type Adapter for that specific type)
     * @throws ConfigDeserializationException if serializer could not deserialize a config entry
     * back to its java value (that happens when sc-cfg is missing a Type Adapter for that
     * specific type)
     * @throws ConfigException if an error occurs while saving the config to the disk
     */
    public static boolean saveDefaults(final Class<?> configClass, boolean overrideIfExists, boolean reloadAfterwards) {
        checkNotNull(configClass, "configClass");
        return configFactory.saveDefaults(configClass, overrideIfExists, reloadAfterwards);
    }

    /**
     * Register a type adapter for serialization/deserialization, and all operations from now on
     * will have this type adapter available.
     *
     * @param type the type that the {@code typeAdapter} handles
     * @param typeAdapter the instance of a type adapter
     * @throws IllegalArgumentException if {@code typeAdapter} is not an instance of a valid type
     * adapter
     */
    public static void registerTypeAdapter(final Type type, final Object typeAdapter) {
        checkNotNull(type, "type");
        checkNotNull(typeAdapter, "typeAdapter");
        gsonFactory.addTypeAdapter(type, typeAdapter);
    }

    /**
     * Register multiple type adapter for serialization/deserialization, and all operations from
     * now on will have those type adapters available.
     *
     * @param typeAdapters map containing the type that the {@code typeAdapter} handles mapped to
     * the instance of a type adapter
     * @throws IllegalArgumentException if any of the values from {@code typeAdapters} map is not
     * an instance of a valid type adapter
     */
    public static void registerTypeAdapters(final Map<? extends Type, Object> typeAdapters) {
        notContainsNull(typeAdapters, "typeAdapters");
        gsonFactory.addTypeAdapters(typeAdapters);
    }
}
