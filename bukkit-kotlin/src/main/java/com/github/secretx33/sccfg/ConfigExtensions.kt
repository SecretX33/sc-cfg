package com.github.secretx33.sccfg


/**
 * Extension for getting the singleton instance of [T].
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register it whenever is convenient for you using [registerConfigs] method.
 *
 * @return T the singleton instance of T
 * @throws [MissingConfigAnnotationException][com.github.secretx33.sccfg.exception.MissingConfigAnnotationException] if `T` class is not annotated with [Configuration][com.github.secretx33.sccfg.api.annotation.Configuration]
 * @throws [MissingNoArgsConstructor][com.github.secretx33.sccfg.exception.MissingNoArgsConstructor] if `T` class does not have a no args constructor
 */
inline fun <reified T : Any> getConfig(): T = Config.getConfig(T::class.java)

/**
 * Lazy extension for getting the singleton instance of [T].
 *
 * **Hint:** if you want to instantiate the configuration class yourself, you can do that, just register it whenever is convenient for you using [registerConfigs] method.
 *
 * @return Lazy<T> lazy delegate of the singleton instance of T
 * @throws [MissingConfigAnnotationException][com.github.secretx33.sccfg.exception.MissingConfigAnnotationException] if `T` class is not annotated with [Configuration][com.github.secretx33.sccfg.api.annotation.Configuration]
 * @throws [MissingNoArgsConstructor][com.github.secretx33.sccfg.exception.MissingNoArgsConstructor] if `T` class does not have a no args constructor
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
