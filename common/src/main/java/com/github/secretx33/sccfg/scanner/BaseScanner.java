package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.api.annotation.AfterReload;
import com.github.secretx33.sccfg.api.annotation.BeforeReload;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BaseScanner implements Scanner {

    protected final Reflections reflections = getGenericReflections();
    protected static final Set<ClassLoader> baseClassLoaders = Sets.immutableOf(BaseScanner.class.getClassLoader(), ClassLoader.getSystemClassLoader(), ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());
    protected Set<ClassLoader> extraClassLoaders;

    @NotNull
    protected Reflections getGenericReflections(final ClassLoader... additionalClassLoaders) {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)
                .addClassLoaders(Sets.toArray(baseClassLoaders, extraClassLoaders))
                .addClassLoaders(additionalClassLoaders)
                .filterInputsBy(new FilterBuilder().excludePattern("com.github.secretx33.sccfg")));
    }

    @NotNull
    protected Reflections getReflections(final Class<?> clazz, final ClassLoader... additionalClassLoaders) {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)
                .addClassLoaders(Sets.toArray(baseClassLoaders, extraClassLoaders))
                .addClassLoaders(additionalClassLoaders)
                .forPackage(clazz.getPackage().getName()));
    }

    @Override
    public Set<Class<?>> getConfigurationClasses() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }

    @Override
    public Set<Class<?>> getRegisterTypeAdapters() {
        return reflections.getTypesAnnotatedWith(RegisterTypeAdapter.class);
    }

    @Override
    public Set<Method> getBeforeReloadMethods(Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getZeroArgsMethods(() -> reflectionForClass.getMethodsAnnotatedWith(BeforeReload.class));
    }

    @Override
    public Set<Method> getAfterReloadMethods(Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getZeroArgsMethods(() -> reflectionForClass.getMethodsAnnotatedWith(AfterReload.class));
    }

    protected Set<Method> getZeroArgsMethods(final Supplier<Set<Method>> methods) {
        return methods.get().stream()
                .filter(method -> method.getParameterCount() == 0)
                .peek(method -> method.setAccessible(true))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Field> getConfigurationFields(Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        final Set<Field> ignoredFields = getIgnoredFields(reflectionForClass);
        return Arrays.stream(clazz.getFields())
                .filter(field -> !ignoredFields.contains(field))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Field> getIgnoredFields(Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getIgnoredFields(reflectionForClass);
    }

    protected Set<Field> getIgnoredFields(Reflections reflectionsForClass) {
        return reflectionsForClass.getFieldsAnnotatedWith(IgnoreField.class);
    }
}
