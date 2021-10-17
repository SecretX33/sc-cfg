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
     * {@link java.util.concurrent.ForkJoinPool#commonPool()}.<br><br>
     *
     * On standalone applications this behaviour is undefined, so always assume that your methods will
     * run async.
     */
    boolean async() default false;
}
