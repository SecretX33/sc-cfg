package com.github.secretx33.sccfg.exception;

/**
 * Thrown when the using the wrong module for the current platform, e.g. using {@code bukkit} module for {@code bungee}
 * platform, or using one of the former for a {@code standalone} platform.
 */
public final class WrongPlatformModuleException extends ConfigException {

    public WrongPlatformModuleException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
