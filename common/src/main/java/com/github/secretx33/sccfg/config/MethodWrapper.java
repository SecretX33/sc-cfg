package com.github.secretx33.sccfg.config;

import java.lang.reflect.Method;
import java.util.Objects;

import static com.github.secretx33.sccfg.util.Preconditions.checkArgument;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public class MethodWrapper {

    private final Method method;
    private final boolean async;

    public MethodWrapper(final Method method, final boolean async) {
        checkArgument(method.isAccessible(), () -> "method needs to be accessible before it can be wrapped, but '" + method.getName() + "' from class '" + method.getDeclaringClass().getCanonicalName() + "' was not set accessible");
        this.method = checkNotNull(method);
        this.async = async;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isAsync() {
        return async;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodWrapper that = (MethodWrapper) o;
        return async == that.async && method.equals(that.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, async);
    }

    @Override
    public String toString() {
        return "MethodWrapper{" +
                "method=" + method +
                ", async=" + async +
                '}';
    }
}
