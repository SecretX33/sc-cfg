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
package com.github.secretx33.sccfg

import com.github.secretx33.sccfg.api.annotation.Configuration
import com.github.secretx33.sccfg.exception.ConfigDeserializationException
import com.github.secretx33.sccfg.exception.ConfigException
import com.github.secretx33.sccfg.exception.ConfigInstanceOverrideException
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException
import com.github.secretx33.sccfg.exception.ConfigSerializationException
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException
import java.lang.reflect.Type
import kotlin.reflect.KClass


/**
 * Extension for getting the singleton instance of config [T]. This method is thread safe.
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register
 * it whenever is convenient for you using [registerConfig] method.
 *
 * @return T the singleton instance of T
 * @throws [MissingConfigAnnotationException] if `T` class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if `T` class does not have a no-args constructor
 */
inline fun <reified T : Any> getConfig(): T = Config.getConfig(T::class.java)

/**
 * Lazy extension for getting the singleton instance of config [T]. This method is thread safe and provides
 * an instance of a thread safe `lazy`.
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register
 * it whenever is convenient for you using [registerConfigs] method.
 *
 * @return Lazy<T> lazy delegate of the singleton instance of config T
 * @throws [MissingConfigAnnotationException] if `T` class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if `T` class does not have a no-args constructor
 */
inline fun <reified T : Any> lazyConfig(): Lazy<T> = lazy { getConfig() }

/**
 * Extension for registering your instance of a configuration class. This method is thread safe, as it
 * guarantees that no overrides can happen when passing as argument instances of configs already registered,
 * but the `ConfigInstanceOverrideException` thrown is only best-effort, so no guarantees can be made about it.
 *
 * @param config T the config instance
 * @return T the config instance
 * @throws [MissingConfigAnnotationException] if `instance` class is not annotated with [Configuration]
 * @throws [ConfigInstanceOverrideException] if `instance` class is already registered
 */
inline fun <reified T : Any> registerConfig(config: T): T = Config.registerConfig(config)

/**
 * Extension for registering multiple instances of a configuration classes.
 *
 * @param configs Array<out Any> the config instances
 * @throws [MissingConfigAnnotationException] if any instance class is not annotated with [Configuration]
 * @throws [ConfigInstanceOverrideException] if there's already a registered instance of the passed instances classes
 */
fun registerConfigs(vararg configs: Any) = Config.registerConfigs(configs)

/**
 * Extension to persist a config instance to the disk (save the current values to the disk).
 *
 * @param config Any the config instance
 * @throws MissingConfigAnnotationException if `config` class is not annotated with [Configuration]
 * @throws ConfigNotInitializedException if `config` is an instance of a class that was not
 * initiated or registered yet
 * @throws ConfigSerializationException if serializer could not serialize a config entry
 * (that happens when sc-cfg is missing a Type Adapter for that specific type)
 * @throws ConfigException if an error occurs while saving the config to the disk
 */
fun saveConfig(config: Any) = Config.saveConfig(config)

/**
 * Extension to persist a config class to the disk (save the current values of that instance to the
 * disk).
 *
 * @param T the class of the config instance that needs to be persisted
 * @throws MissingConfigAnnotationException if config class `T` is not annotated with [Configuration]
 * @throws ConfigNotInitializedException if config class `T` is an instance of a config that was not
 * initiated or registered yet
 * @throws ConfigSerializationException if serializer could not serialize a config entry
 * (that happens when sc-cfg is missing a Type Adapter for that specific type)
 * @throws ConfigException if an error occurs while saving the config to the disk
 */
inline fun <reified T : Any> saveConfig() = Config.saveConfig(T::class.java)

/**
 * Extension to persist multiple config class to the disk (save the current values of passed instances
 * to the disk).
 *
 * @param config Array<out Any> the config instances that need to be persisted
 * @throws MissingConfigAnnotationException if any instance class is not annotated with [Configuration]
 * @throws ConfigNotInitializedException if any of the passed instances is an instance of
 * non-registered config class
 * @throws ConfigSerializationException if serializer could not serialize a config entry
 * (that happens when sc-cfg is missing a Type Adapter for that specific type)
 * @throws ConfigException if an error occurs while saving the config to the disk
 */
fun saveConfigs(vararg config: Any) = Config.saveConfigs(config)

/**
 * Save the default values of this config instance to the disk.
 *
 * @param config Any
 * @param overrideIfExists Boolean if true, the config file will be overwritten if it exists, else it
 * won't be touched
 * @param reloadAfterwards Boolean if config instance should be reloaded to reflect the new, default
 * values that were saved to the disk
 * @return Boolean true if the file was saved to the disk, false if the file already existed or some
 * exception has occurred
 * @throws ConfigSerializationException if serializer could not serialize a config entry
 * (that happens when sc-cfg is missing a Type Adapter for that specific type)
 * @throws ConfigDeserializationException if serializer could not deserialize a config entry
 * back to its java value (that happens when sc-cfg is missing a Type Adapter for that
 * specific type)
 * @throws ConfigException if an error occurs while saving the config to the disk
 */
fun saveDefaults(
    config: Any,
    overrideIfExists: Boolean = false,
    reloadAfterwards: Boolean = true,
): Boolean = Config.saveDefaults(config, overrideIfExists, reloadAfterwards)

/**
 * Save the default values of this config class to the disk.
 *
 * @param configClass KClass<out Any> the config class
 * @param overrideIfExists Boolean if true, the config file will be overwritten if it exists, else it
 * won't be touched
 * @param reloadAfterwards Boolean if config instance should be reloaded to reflect the new, default
 * values that were saved to the disk
 * @return Boolean true if the file was saved to the disk, false if the file already existed or some
 * exception has occurred
 * @throws ConfigSerializationException if serializer could not serialize a config entry
 * (that happens when sc-cfg is missing a Type Adapter for that specific type)
 * @throws ConfigDeserializationException if serializer could not deserialize a config entry
 * back to its java value (that happens when sc-cfg is missing a Type Adapter for that
 * specific type)
 * @throws ConfigException if an error occurs while saving the config to the disk
 */
fun saveDefaults(
    configClass: KClass<out Any>,
    overrideIfExists: Boolean = false,
    reloadAfterwards: Boolean = true,
): Boolean = Config.saveDefaults(configClass.java, overrideIfExists, reloadAfterwards)

/**
 * Extension to register a Gson type adapter for a given type.
 *
 * @param type Type the type which `typeAdapter` serializes and/or deserializes
 * @param typeAdapter Any an instance of a type adapter
 * @throws IllegalArgumentException if `typeAdapter` is not an instance of a Gson type adapter
 */
fun registerTypeAdapter(type: Type, typeAdapter: Any) = Config.registerTypeAdapter(type, typeAdapter)

/**
 * Extension to register multiple Gson type adapter at once.
 *
 * @param typeAdapters Map<out Type, Any> the map holding your custom type adapters, each key is the
 * type which the correspondent value is able to serialize and/or deserialize
 * @throws IllegalArgumentException if any of the `typeAdapters` values is not an instance of a Gson
 * type adapter
 */
fun registerTypeAdapters(typeAdapters: Map<out Type, Any>) = Config.registerTypeAdapters(typeAdapters)

