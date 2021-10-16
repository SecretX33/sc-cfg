package com.github.secretx33.sccfg.util;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class Packages {

    private Packages() {}

    public static boolean isPackageWithin(final Package pkg, final String otherPkg) {
        checkNotNull(pkg, "pkg");
        checkNotNull(otherPkg, "otherPkg");

        final String[] slitClassPkg = pkg.getName().split("\\.");
        final String[] slitPkg = otherPkg.split("\\.");

        if(slitClassPkg.length < slitPkg.length) return false;

        for(int i = 0; i < slitPkg.length; i++) {
            if (!slitPkg[i].equals(slitClassPkg[i])) {
                return false;
            }
        }
        return true;
    }

    public static boolean isPackageNotWithin(final Package pkg, final String otherPkg) {
        checkNotNull(pkg, "pkg");
        checkNotNull(otherPkg, "otherPkg");

        return !isPackageWithin(pkg, otherPkg);
    }

    public static boolean isClassWithinPackage(final Class<?> clazz, final String pkg) {
        checkNotNull(clazz, "clazz");
        checkNotNull(pkg, "pkg");

        return isPackageWithin(clazz.getPackage(), pkg);
    }

    public static boolean isClassNotWithinPackage(final Class<?> clazz, final String pkg) {
        checkNotNull(clazz, "clazz");
        checkNotNull(pkg, "pkg");

        return !isPackageWithin(clazz.getPackage(), pkg);
    }
}
