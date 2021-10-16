package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.lang.reflect.Field;
import java.util.Set;

public interface Scanner {

    Set<Class<?>> getConfigurationClasses();

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
    Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz);
    Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz);
    Set<Field> getConfigurationFields(final Class<?> clazz);
    Set<Field> getIgnoredFields(final Class<?> clazz);
}
