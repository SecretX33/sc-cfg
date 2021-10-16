package com.github.secretx33.sccfg.util;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Packages {

    private Packages() {}

    public static boolean isClassWithinPackage(final Class<?> clazz, final String pkg) {
        checkNotNull(clazz, "clazz");
        checkNotNull(pkg, "pkg");

        final String[] slitClassPkg = clazz.getPackage().getName().split("\\.");
        final String[] slitPkg = pkg.split("\\.");

        if(slitClassPkg.length < slitPkg.length) return false;

        for(int i = 0; i < slitPkg.length; i++) {
            if (!slitPkg[i].equals(slitClassPkg[1])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isClassNotWithinPackage(final Class<?> clazz, final String pkg) {
        checkNotNull(clazz, "clazz");
        checkNotNull(pkg, "pkg");

        return !isClassWithinPackage(clazz, pkg);
    }
}
