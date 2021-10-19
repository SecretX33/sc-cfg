package com.github.secretx33.sccfg.config;

public interface ConfigFactory {

    <T> ConfigWrapper<T> getWrapper(Class<T> configClazz);

    void registerInstance(Object instance);

    void saveInstance(Object instance);

    void saveInstance(Class<?> configClazz);
}
