package com.github.secretx33.sccfg.scanner;

import com.github.secretx33.sccfg.api.annotation.AfterReload;
import com.github.secretx33.sccfg.api.annotation.BeforeReload;
import com.github.secretx33.sccfg.api.annotation.Configuration;
import com.github.secretx33.sccfg.api.annotation.IgnoreField;
import com.github.secretx33.sccfg.api.annotation.RegisterTypeAdapter;
import com.github.secretx33.sccfg.config.MethodWrapper;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.util.Packages;
import com.github.secretx33.sccfg.util.Sets;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class BaseScanner implements Scanner {

    private static final String LIBRARY_CLASSPATH = "com.github.secretx33.sccfg";
    private static final Set<ClassLoader> BASE_CLASSLOADERS = Sets.immutableOf(BaseScanner.class.getClassLoader(), ClassLoader.getSystemClassLoader(), ClasspathHelper.contextClassLoader(), ClasspathHelper.staticClassLoader());

    private final Reflections reflections;
    private final Set<ClassLoader> extraClassLoaders;
    private final String basePackage;

    public BaseScanner(final String basePackage) {
        this.extraClassLoaders = Collections.emptySet();
        this.basePackage = checkNotNull(basePackage, "basePath");
        this.reflections = getGenericReflections();
    }

    public BaseScanner(final String basePackage, final Set<ClassLoader> extraClassLoaders) {
        this.basePackage = checkNotNull(basePackage, "basePath");
        this.extraClassLoaders = checkNotNull(extraClassLoaders, "extraClassLoaders");
        this.reflections = getGenericReflections();
    }

    @NotNull
    protected Reflections getGenericReflections() {
        return new Reflections(new ConfigurationBuilder()
                .addScanners(Scanners.TypesAnnotated, Scanners.MethodsAnnotated, Scanners.FieldsAnnotated)
                .addClassLoaders(Sets.toArray(ClassLoader.class, BASE_CLASSLOADERS, extraClassLoaders))
                .forPackage(basePackage));
    }

    @Override
    public Set<Class<?>> getConfigurationClasses() {
        return reflections.getTypesAnnotatedWith(Configuration.class);
    }

    @Override
    public Set<Class<?>> getBaseRegisterTypeAdapters() {
        return reflections.getTypesAnnotatedWith(RegisterTypeAdapter.class).stream()
                .filter(clazz -> Packages.isClassWithinPackage(clazz, LIBRARY_CLASSPATH))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Class<?>> getCustomRegisterTypeAdapters() {
        return reflections.getTypesAnnotatedWith(RegisterTypeAdapter.class).stream()
                .filter(clazz -> Packages.isClassNotWithinPackage(clazz, LIBRARY_CLASSPATH))
                .collect(Collectors.toSet());
    }

    @Override
    public Set<MethodWrapper> getBeforeReloadMethods(final Class<?> clazz) {
        return getZeroArgsInstanceMethods(() -> getMethodsAnnotatedWith(clazz, BeforeReload.class),
            method -> {
                final BeforeReload reloadAnnotation = method.getDeclaredAnnotation(BeforeReload.class);
                final boolean async = reloadAnnotation.async();
                return new MethodWrapper(method, async);
            });
    }

    @Override
    public Set<MethodWrapper> getAfterReloadMethods(final Class<?> clazz) {
        return getZeroArgsInstanceMethods(() -> getMethodsAnnotatedWith(clazz, AfterReload.class),
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
        final Set<Field> ignoredFields = getIgnoredFields(clazz);
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers())
                        && !ignoredFields.contains(field))
                .map(this::turnAccessibleNonField)
                .collect(Collectors.toSet());
    }

    private Field turnAccessibleNonField(final Field field) {
        field.setAccessible(true);
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (final NoSuchFieldException e) {
            // Java 9+ throws NoSuchFieldException for that operation, is safe to ignore it
        } catch (final ReflectiveOperationException e) {
            throw new ConfigException(e);
        }
        return field;
    }

    @Override
    public Set<Field> getIgnoredFields(final Class<?> clazz) {
        return getFieldsAnnotatedWith(clazz, IgnoreField.class).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }

    private Set<Method> getMethodsAnnotatedWith(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.getDeclaredAnnotation(annotationClass) != null)
                .collect(Collectors.toSet());
    }

    private Set<Field> getFieldsAnnotatedWith(final Class<?> clazz, final Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.getDeclaredAnnotation(annotationClass) != null)
                .collect(Collectors.toSet());
    }
}
