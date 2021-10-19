/*
 * Copyright (C) 2021 SecretX <notyetmidnight@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
