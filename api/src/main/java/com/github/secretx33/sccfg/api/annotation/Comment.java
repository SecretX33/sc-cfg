/*
 * Copyright (C) 2021-2022 SecretX <notyetmidnight@gmail.com>
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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set the property comments.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {

    /**
     * Comments to be placed on top of this property in the config file, each line being represented
     * either by an entry of the array, or by {@code \n} in the same string. While they can be anything,
     * they're usually used for explaining what the property is used for, or what it represents in your
     * code.<br><br>
     *
     * Keep in mind that this won't be applied to the property if the underlying file type does not
     * support comments.<br>
     *
     * @return the comment block to be placed in the config file, right on top of the property
     */
    String[] value();
}
