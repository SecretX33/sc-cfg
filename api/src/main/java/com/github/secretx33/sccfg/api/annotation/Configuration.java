package com.github.secretx33.sccfg.api.annotation;

import com.github.secretx33.sccfg.api.FieldNameStrategy;
import com.github.secretx33.sccfg.api.FileType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    String value() default "";

    FileType type() default FileType.YAML;

    FieldNameStrategy nameStrategy() default FieldNameStrategy.AS_IS;
}
