package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.lang.reflect.Field;
import java.util.Set;

public interface Scanner {

    /**
     * Return a set containing all type adapters classes that are <b>within</b> the sccfg
     * classpath.
     */
    Set<Class<?>> getBaseRegisterTypeAdapters();

    /**
     * Return a set containing all type adapters classes that are <b>not</b> in the sccfg
     * classpath.
     */
    Set<Class<?>> getCustomRegisterTypeAdapters();

    /**
     * Return a set containing all methods that should be run before reloading the config instance.
     */
    Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz);

    /**
     * Return a set containing all methods that should be run after reloading the config instance.
     */
    Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz);

    /**
     * Return a set containing all field that should be serialized.
     */
    Set<Field> getConfigurationFields(final Class<?> clazz);

    /**
     * Return a set containing all field that should not be serialized.
     */
    Set<Field> getIgnoredFields(final Class<?> clazz);
}
