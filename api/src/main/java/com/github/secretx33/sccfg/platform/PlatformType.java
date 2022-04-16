package com.github.secretx33.sccfg.platform;

import org.jetbrains.annotations.Nullable;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * Enum that maps all supported platforms to a class only present when running that given platform. It also holds
 * what SC-CFG class should be used for that specific platform.<br><br>
 *
 * Order of declarations here matter.
 */
enum PlatformType {
    SPIGOT("org.spigotmc.SpigotConfig", "com.github.secretx33.sccfg.platform.BukkitPlatform"),
    BUNGEE("net.md_5.bungee.config.Configuration", "com.github.secretx33.sccfg.platform.BungeePlatform"),
    STANDALONE(null, "com.github.secretx33.sccfg.platform.StandalonePlatform");

    @Nullable
    private final String testClass;
    private final String platformClass;

    PlatformType(@Nullable final String testClass, final String platformClass) {
        this.testClass = testClass;
        this.platformClass = platformClass;
    }

    public String getPlatformClass() {
        return platformClass;
    }

    static final PlatformType ACTUAL;

    static {
        PlatformType platformType = null;
        for (final PlatformType type : values()) {
            try {
                if (type.testClass != null) {
                    Class.forName(type.testClass);
                }
                platformType = type;
                break;
            } catch (final ClassNotFoundException ignored) {
            }
        }
        ACTUAL = checkNotNull(platformType, "platformType");
    }
}
