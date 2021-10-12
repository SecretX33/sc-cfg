package com.github.secretx33.sccfg.factory;

import com.github.secretx33.sccfg.config.ConfigWrapper;

public interface ConfigFactory {

    <T> ConfigWrapper<T> getWrapper(Class<T> clazz);
}
