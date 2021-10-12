package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.api.annotation.AfterReload;
import com.github.secretx33.sccfg.api.annotation.BeforeReload;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BaseScanner implements Scanner {

    protected static final Set<ClassLoader> BASE_CLASSLOADERS = Sets.immutableOf(BaseScanner.class.getClassLoader(), ClassLoader.getSystemClassLoader(), ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());

    protected final Reflections reflections = getGenericReflections();
    protected Set<ClassLoader> extraClassLoaders = Collections.emptySet();

    @NotNull
    protected Reflections getGenericReflections(final ClassLoader... additionalClassLoaders) {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)
                .addClassLoaders(Sets.toArray(BASE_CLASSLOADERS, extraClassLoaders))
                .addClassLoaders(additionalClassLoaders)
                .filterInputsBy(new FilterBuilder().excludePattern("com.github.secretx33.sccfg")));
    }

    @NotNull
    protected Reflections getReflections(final Class<?> clazz, final ClassLoader... additionalClassLoaders) {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)
                .addClassLoaders(Sets.toArray(BASE_CLASSLOADERS, extraClassLoaders))
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
    public Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getZeroArgsInstanceMethods(() -> reflectionForClass.getMethodsAnnotatedWith(BeforeReload.class),
                method -> {
                    final BeforeReload reloadAnnotation = method.getDeclaredAnnotation(BeforeReload.class);
                    final boolean async = reloadAnnotation.async();
                    return new MethodWrapper(method, async);
                });
    }

    @Override
    public Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getZeroArgsInstanceMethods(() -> reflectionForClass.getMethodsAnnotatedWith(AfterReload.class),
                method -> {
                    final AfterReload reloadAnnotation = method.getDeclaredAnnotation(AfterReload.class);
                    final boolean async = reloadAnnotation.async();
                    return new MethodWrapper(method, async);
                });
    }

    protected Set<MethodWrapper> getZeroArgsInstanceMethods(
        final Supplier<Set<Method>> methods,
        final Function<Method, MethodWrapper> mapper
    ) {
        return methods.get().stream()
                .filter(method -> method.getParameterCount() == 0
                        && !Modifier.isStatic(method.getModifiers()))
                .peek(method -> method.setAccessible(true))
                .map(mapper)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Field> getConfigurationFields(final Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getConfigurationFields(clazz, reflectionForClass);
    }

    @NotNull
    protected Set<Field> getConfigurationFields(final Class<?> clazz, final Reflections reflectionsForClass) {
        final Set<Field> ignoredFields = getIgnoredFields(reflectionsForClass);
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers())
                        && !ignoredFields.contains(field))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Field> getIgnoredFields(final Class<?> clazz) {
        final Reflections reflectionForClass = getReflections(clazz);
        return getIgnoredFields(reflectionForClass);
    }

    protected Set<Field> getIgnoredFields(final Reflections reflectionsForClass) {
        return reflectionsForClass.getFieldsAnnotatedWith(IgnoreField.class).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }
}
