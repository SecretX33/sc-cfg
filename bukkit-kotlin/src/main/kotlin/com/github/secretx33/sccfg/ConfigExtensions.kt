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

import java.lang.reflect.Type


/**
 * Extension for getting the singleton instance of [T].
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register it whenever is convenient for you using [registerConfigs] method.
 *
 * @return T the singleton instance of T
 * @throws [MissingConfigAnnotationException][com.github.secretx33.sccfg.exception.MissingConfigAnnotationException] if `T` class is not annotated with [Configuration][com.github.secretx33.sccfg.api.annotation.Configuration]
 * @throws [MissingNoArgsConstructor][com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException] if `T` class does not have a no args constructor
 */
inline fun <reified T : Any> getConfig(): T = Config.getConfig(T::class.java)

/**
 * Lazy extension for getting the singleton instance of [T].
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register it whenever is convenient for you using [registerConfigs] method.
 *
 * @return Lazy<T> lazy delegate of the singleton instance of T
 * @throws [MissingConfigAnnotationException][com.github.secretx33.sccfg.exception.MissingConfigAnnotationException] if `T` class is not annotated with [Configuration][com.github.secretx33.sccfg.api.annotation.Configuration]
 * @throws [MissingNoArgsConstructor][com.github.secretx33.sccfg.exception.MissingNoArgsConstructorException] if `T` class does not have a no args constructor
 */
inline fun <reified T : Any> lazyConfig(): Lazy<T> = lazy { getConfig() }

/**
 * Extension for registering your instance of a configuration class.
 */
inline fun <reified T : Any> registerConfig(instance: T): T = Config.registerConfig(instance)

/**
 * Extension for registering your instance of a configuration class.
 */
fun registerConfigs(vararg instances: Any) = Config.registerConfigs(instances)

fun saveConfig(config: Any) = Config.saveConfig(config)

inline fun <reified T : Any> saveConfig() = Config.saveConfig(T::class.java)

fun saveConfigs(vararg config: Any) = Config.saveConfigs(config)

fun registerTypeAdapter(type: Type, typeAdapter: Any) = Config.registerTypeAdapter(type, typeAdapter)

fun registerTypeAdapters(typeAdapters: Map<out Type, Any>) = Config.registerTypeAdapters(typeAdapters)

