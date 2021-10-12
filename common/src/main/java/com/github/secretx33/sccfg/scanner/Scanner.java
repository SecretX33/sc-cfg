package com.github.secretx33.sccfg.scanner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

public interface Scanner {

    Set<Class<?>> getConfigurationClasses();
    Set<Class<?>> getRegisterTypeAdapters();
    Set<Method> getBeforeReloadMethods(final Class<?> clazz);
    Set<Method> getAfterReloadMethods(final Class<?> clazz);
    Set<Field> getConfigurationFields(final Class<?> clazz);
    Set<Field> getIgnoredFields(final Class<?> clazz);
}
