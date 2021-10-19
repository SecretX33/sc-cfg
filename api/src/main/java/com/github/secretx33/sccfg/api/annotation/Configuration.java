package com.github.secretx33.sccfg.api.annotation;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.api.NameStrategy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to mark a class as a configuration class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {

    /**
     * The relative path and name of the config class. If not specified, the class name will be used
     * instead.<br><br>
     *
     * {@code myfolder/myfile} will create a folder called "myfolder" and a file called "myfile"
     * on the relative path for that application (for Bukkit, it'll be inside plugin's data folder, and
     * for standalone applications it'll be relative to folder where the software is running on).
     * You can also explicitly provide extensions on the name, if you want, so, for example, {@code myfile.yml}
     * is a valid name for a configuration of type {@link com.github.secretx33.sccfg.api.FileType#YAML}
     */
    String value() default "";

    /**
     * Specifies what file type that config should be.
     */
    FileType type() default FileType.YAML;

    /**
     * Specifies what kind of transformation should be applied to the name of the properties inside
     * the configuration file, default is "none".
     */
    NameStrategy nameStrategy() default NameStrategy.NONE;
}
