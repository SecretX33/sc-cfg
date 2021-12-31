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
package com.github.secretx33.sccfg.api.annotation;

import com.github.secretx33.sccfg.api.FileType;
import com.github.secretx33.sccfg.api.Naming;

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
     * for standalone applications it'll be relative to folder where the software is running on).<br><br>
     *
     * You can also explicitly provide extensions on the name, if you want, so, for example, {@code myfile.yml}
     * is a valid name for a configuration of type {@link com.github.secretx33.sccfg.api.FileType#YAML}.
     */
    String value() default "";

    /**
     * Alias for {@link Configuration#value()}.
     */
    String name() default "";

    /**
     * Specifies what file type that config should be.
     */
    FileType type() default FileType.YAML;

    /**
     * Specifies what kind of transformation should be applied to the name of ALL properties inside
     * the configuration file. By default, the names are kept exactly as provided.
     */
    Naming naming() default Naming.NONE;

    /**
     * Comments to be placed as header in the config file, each line being represented by an entry of
     * the array. While they can be anything, they're usually used for explaining what the file is for,
     * adding your own logo, etc.<br><br>
     *
     * Keep in mind that this won't be applied to the config file if the underlying file type does not
     * support comments.<br><br>
     *
     * @return the comment block to be placed as the first thing in the config file, before any property
     */
    String[] header() default {};
}
