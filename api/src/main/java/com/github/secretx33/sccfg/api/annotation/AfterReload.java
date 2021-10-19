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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be run after each config reload. Use this to perform general tasks, like sending
 * messages on the console ({@code config X reloaded}), perform checks on illegal values and replace/warn
 * the user, and so on.
 *
 * <br><br>This annotation should be preferred over {@link com.github.secretx33.sccfg.api.annotation.BeforeReload}
 * because it doesn't delay the config reload.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterReload {

    /**
     * This option is platform dependent. By default, on bukkit, all methods run on Bukkit main thread,
     * but you can change that by setting this option to true, which will cause your method to be run on
     * {@link java.util.concurrent.ForkJoinPool#commonPool() ForkJoinPool#commonPool()}.<br><br>
     *
     * On standalone applications this behaviour is undefined, so always assume that your methods will
     * run async.
     */
    boolean async() default false;
}
