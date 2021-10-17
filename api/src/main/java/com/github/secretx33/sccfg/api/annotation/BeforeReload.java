package com.github.secretx33.sccfg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method to be run <b>before</b> each config reload. Use this to perform specific tasks that
 * requires to be run before reloading the config.<br><br>
 *
 * Keep in mind that the reload operation will wait for all the methods annotated with {@code BeforeReload}
 * up to 4 seconds, even if the method has {@link BeforeReload#async()} set to {@code true}, so
 * try to use this only for quick operations/checks, and let file reading, network calls, etc. to be
 * performed by the {@link com.github.secretx33.sccfg.api.annotation.AfterReload} methods, or run these
 * expensive tasks on another thread manually.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BeforeReload {

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
