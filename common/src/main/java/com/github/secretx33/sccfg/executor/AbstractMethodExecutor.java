package com.github.secretx33.sccfg.executor;

import com.github.secretx33.sccfg.config.MethodWrapper;

import java.lang.reflect.Method;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractMethodExecutor {

    protected Logger logger;

    public void runCatching(final Object instance, final MethodWrapper wrapper) {
        final Method method = wrapper.getMethod();
        try {
            method.invoke(instance);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown while executing method '" + method.getName() + "' of class " + method.getDeclaringClass().getCanonicalName(), e);
        }
    }

    public void runCatching(final Object instance, final MethodWrapper wrapper, final CountDownLatch latch) {
        final Method method = wrapper.getMethod();
        try {
            method.invoke(instance);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An exception was thrown while executing method '" + method.getName() + "' of class " + method.getDeclaringClass().getCanonicalName(), e);
        } finally {
            latch.countDown();
        }
    }

    public Runnable runnerCatching(final Object instance, final MethodWrapper wrapper, final CountDownLatch latch) {
        return () -> runCatching(instance, wrapper, latch);
    }
}
