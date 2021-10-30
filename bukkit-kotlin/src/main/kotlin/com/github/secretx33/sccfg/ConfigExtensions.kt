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
import com.github.secretx33.sccfg.exception.ConfigNotInitializedException
import com.github.secretx33.sccfg.exception.ConfigOverrideException
import com.github.secretx33.sccfg.exception.MissingConfigAnnotationException
import com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException
import java.lang.reflect.Type


/**
 * Extension for getting the singleton instance of config [T].
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
 * Lazy extension for getting the singleton instance of config [T].
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
 * Extension for registering your instance of a configuration class.
 *
 * @param instance T the config instance
 * @return T the config instance
 * @throws [ConfigOverrideException] if `instance` class is already registered
 * @throws [MissingConfigAnnotationException] if `instance` class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if `instance` class does not have a no-args constructor
 */
inline fun <reified T : Any> registerConfig(instance: T): T = Config.registerConfig(instance)

/**
 * Extension for registering multiple instances of a configuration classes.
 *
 * @param instances Array<out Any> the config instances
 * @throws [ConfigOverrideException] if there's already a registered instance of the passed instances classes
 * @throws [MissingConfigAnnotationException] if any instance class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if any instance class does not have a no-args constructor
 */
fun registerConfigs(vararg instances: Any) = Config.registerConfigs(instances)

/**
 * Extension to persist a config instance to the disk (save the current values to the disk).
 *
 * @param config Any the config instance
 * @throws [ConfigNotInitializedException] if `config` is an instance of a config that was not registered.
 * @throws [MissingConfigAnnotationException] if `config` class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if `config` class does not have a no-args constructor
 */
fun saveConfig(config: Any) = Config.saveConfig(config)


/**
 * Extension to persist a config class to the disk (save the current values of that instance to the
 * disk).
 *
 * @param T the class of the config instance that needs to be persisted
 * @throws [ConfigNotInitializedException] if `config` is an instance of a config that was not initiated
 * or registered.
 * @throws [MissingConfigAnnotationException] if `config` class is not annotated with [Configuration]
 * @throws [MissingNoArgsConstructorException] if `config` class does not have a no-args constructor
 */
inline fun <reified T : Any> saveConfig() = Config.saveConfig(T::class.java)

/**
 * Extension to persist multiple config class to the disk (save the current values of passed instances
 * to the disk).
 *
 * @param config Array<out Any> the config instances that need to be persisted
 * @throws ConfigNotInitializedException if any of the passed instances is an instance of
 * non-registered config class
 * @throws MissingConfigAnnotationException if any instance class is not annotated with [Configuration]
 * @throws MissingNoArgsConstructorException if any instance class does not have a no-args constructor
 */
fun saveConfigs(vararg config: Any) = Config.saveConfigs(config)

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

