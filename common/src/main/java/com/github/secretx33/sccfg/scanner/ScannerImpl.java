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
package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.api.annotation.AfterReload;
import com.github.secretx33.sccfg.api.annotation.BeforeReload;
import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.github.secretx33.sccfg.api.annotation.PathComment;
import com.github.secretx33.sccfg.api.annotation.PathComments;
import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.config.MethodWrapperImpl;
import com.github.secretx33.sccfg.exception.ConfigReflectiveOperationException;
import com.github.secretx33.sccfg.util.Packages;
import com.github.secretx33.sccfg.util.Sets;
import com.google.common.collect.Streams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;
import static com.github.secretx33.sccfg.util.Preconditions.notContainsNull;

public class ScannerImpl implements Scanner {

    private static final String LIBRARY_CLASSPATH = "com.github.secretx33.sccfg";
    private static final Set<ClassLoader> BASE_CLASSLOADERS = Sets.of(ScannerImpl.class.getClassLoader(), ClassLoader.getSystemClassLoader(), ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());
    @Nullable
    private static final Field MODIFIERS_FIELD;

    static {
        Field modifiersField = null;
        try {
            modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
        } catch (final NoSuchFieldException e) {
            // Java 9+ throws NoSuchFieldException for that operation, is safe to ignore it
        } finally {
            MODIFIERS_FIELD = modifiersField;
        }
    }

    private final Set<ClassLoader> extraClassLoaders;
    private final String basePackage;
    /**
     * Type adapters provided by this library.
     */
    private final Set<Class<?>> baseTypeAdapters;
    /**
     * Type adapters provided by the user.
     */
    private final Set<Class<?>> customTypeAdapters;

    public ScannerImpl(final Object mainInstance) {
        this(checkNotNull(mainInstance, "mainInstance").getClass().getPackage().getName(), Sets.of(mainInstance.getClass().getClassLoader()));
    }

    public ScannerImpl(final String basePackage, final Set<ClassLoader> extraClassLoaders) {
        this.basePackage = checkNotNull(basePackage, "basePath");
        this.extraClassLoaders = notContainsNull(extraClassLoaders, "extraClassLoaders");
        final Reflections reflections = getGenericReflections();
        baseTypeAdapters = Sets.filter(reflections.getTypesAnnotatedWith(RegisterTypeAdapter.class),
                clazz -> Packages.isClassWithinPackage(clazz, LIBRARY_CLASSPATH));
        customTypeAdapters = Sets.filter(reflections.getTypesAnnotatedWith(RegisterTypeAdapter.class),
                clazz -> Packages.isClassNotWithinPackage(clazz, LIBRARY_CLASSPATH));
    }

    @NotNull
    protected Reflections getGenericReflections() {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated)
                .addClassLoaders(Sets.toArray(ClassLoader.class, BASE_CLASSLOADERS, extraClassLoaders))
                .forPackage(basePackage));
    }

    @Override
    public Set<Class<?>> getBaseRegisterTypeAdapters() {
        return baseTypeAdapters;
    }

    @Override
    public Set<Class<?>> getCustomRegisterTypeAdapters() {
        return customTypeAdapters;
    }

    @Override
    public Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        return getZeroArgsInstanceMethods(getMethodsAnnotatedWith(clazz, BeforeReload.class),
                method -> {
                    final BeforeReload reloadAnnotation = method.getDeclaredAnnotation(BeforeReload.class);
                    final boolean async = reloadAnnotation.async();
                    return new MethodWrapperImpl(method, async);
                });
    }

    @Override
    public Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        return getZeroArgsInstanceMethods(getMethodsAnnotatedWith(clazz, AfterReload.class),
                method -> {
                    final AfterReload reloadAnnotation = method.getDeclaredAnnotation(AfterReload.class);
                    final boolean async = reloadAnnotation.async();
                    return new MethodWrapperImpl(method, async);
                });
    }

    protected Set<MethodWrapper> getZeroArgsInstanceMethods(
        final Stream<Method> methods,
        final Function<Method, MethodWrapper> mapper
    ) {
        return methods.filter(method -> method.getParameterCount() == 0
                        && !Modifier.isStatic(method.getModifiers()))
                .peek(method -> method.setAccessible(true))
                .map(mapper)
                .collect(Sets.toSet());
    }

    @Override
    public Set<Field> getConfigurationFields(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        final Set<Field> ignoredFields = getIgnoredFields(clazz);
        return getAllMembers(clazz, Class::getDeclaredFields)
                .filter(field -> !Modifier.isStatic(field.getModifiers())
                        && !Modifier.isTransient(field.getModifiers())
                        && !ignoredFields.contains(field))
                .map(this::turnAccessibleNonFinalField)
                .collect(Sets.toSet());
    }

    private Field turnAccessibleNonFinalField(final Field field) {
        field.setAccessible(true);
        if (MODIFIERS_FIELD != null) {
            try {
                MODIFIERS_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            } catch (final ReflectiveOperationException e) {
                throw new ConfigReflectiveOperationException(e);
            }
        }
        return field;
    }

    @Override
    public Set<Field> getIgnoredFields(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");
        return getFieldsAnnotatedWith(clazz, IgnoreField.class)
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Sets.toSet());
    }

    @Override
    public Collection<Class<?>> getClassAndSubclasses(final Class<?> clazz) {
        checkNotNull(clazz, "clazz");

        // class does not inherit from another class, so just go through its fields
        if (clazz.getSuperclass() == null || Object.class.equals(clazz.getSuperclass())) {
            return Sets.of(clazz);
        }

        // collect all classes incl. parents
        Class<?> currentClass = clazz;
        final Set<Class<?>> classes = new LinkedHashSet<>();
        while (currentClass != null && !Object.class.equals(currentClass)) {
            classes.add(currentClass);
            currentClass = currentClass.getSuperclass();
        }
        return Collections.unmodifiableSet(classes);
    }

    @Override
    public Collection<PathComment> getPathCommentFromClassAndAllFields(final Class<?> clazz, final Collection<Field> configFields) {
        final Collection<Class<?>> classAndSubs = getClassAndSubclasses(clazz);

        final Stream<PathComment> classPathComments = extractPathComments(classAndSubs);
        final Stream<PathComment> classPathComment = extractPathComment(classAndSubs);
        final Stream<PathComment> fieldPathComments = extractPathComments(configFields);
        final Stream<PathComment> fieldPathComment = extractPathComment(configFields);

        return Streams.concat(classPathComment, classPathComments, fieldPathComment, fieldPathComments)
                .collect(Collectors.toList());
    }

    private Stream<PathComment> extractPathComment(final Collection<? extends AnnotatedElement> members) {
        return members.stream()
                .map(field -> field.getDeclaredAnnotation(PathComment.class))
                .filter(Objects::nonNull);
    }

    private Stream<PathComment> extractPathComments(final Collection<? extends AnnotatedElement> members) {
        return members.stream()
                .map(field -> field.getDeclaredAnnotation(PathComments.class))
                .filter(Objects::nonNull)
                .flatMap(annot -> Arrays.stream(annot.value()));
    }

    private Stream<Method> getMethodsAnnotatedWith(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {
        return getAllMembers(clazz, Class::getDeclaredMethods)
                .filter(method -> method.getDeclaredAnnotation(annotationClass) != null);
    }

    private Stream<Field> getFieldsAnnotatedWith(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {
        return getAllMembers(clazz, Class::getDeclaredFields)
                .filter(field -> field.getDeclaredAnnotation(annotationClass) != null);
    }

    private <T extends Member> Stream<T> getAllMembers(final Class<?> clazz, final Function<Class<?>, T[]> selector) {
        final Collection<Class<?>> classes = getClassAndSubclasses(clazz);

        // and add all members from all classes (incl. parents) if they were not added yet
        final Set<T> members = new LinkedHashSet<>();
        // the check is done by keeping track of the added members' names
        final Set<String> memberNames = new HashSet<>();

        classes.stream().sequential().flatMap(clz -> Arrays.stream(selector.apply(clz)))
            .forEachOrdered(member -> {
                if (memberNames.add(member.getName())) {
                    members.add(member);
                }
            });
        return members.stream().sequential();
    }
}
