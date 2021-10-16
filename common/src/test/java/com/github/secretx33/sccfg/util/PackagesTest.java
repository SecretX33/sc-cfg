package com.github.secretx33.sccfg.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PackagesTest {

    private Package mockedPackage;

    @BeforeEach
    public void setup() {
        mockedPackage = mock(Package.class);
    }

    @Test
    public void givenPackage_whenIsTheEqualToTheOtherPackage_thenReturnTrue(){
        when(mockedPackage.getName()).thenReturn("com.github.secretx33.sccfg");

        final boolean result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg");
        assertTrue(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "com.github.secretx33.sccfg.anotherpackage",
        "com.github.secretx33.sccfg.serialization.typeadapters",
        "com.github.secretx33.sccfg.yes.no.yes",
        "com.github.secretx33.sccfg.this.should.be.a.very.long.package",
    })
    public void givenPackage_whenIsDeeperRelatedToOtherPackage_thenReturnTrue(){
        when(mockedPackage.getName()).thenReturn("com.github.secretx33.sccfg.serialization.typeadapters");

        final boolean result =  Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg");
        assertTrue(result);
    }

    @Test
    public void givenPackage_whenIsWithinHigherPackage_thenReturnFalse(){
        when(mockedPackage.getName()).thenReturn("com.github.secretx33");

        final boolean result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg");
        assertFalse(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"",
        "   ",
        "com.github.secretx33.anothersccfg",
        "com.github.secretx33.anothersccfg.serialization.typeadapters",
        "me.anotherperson.coolproject"
    })
    public void givenPackage_whenNotWithinOtherPackage_thenReturnFalse(final String pkg){
        when(mockedPackage.getName()).thenReturn(pkg);

        final boolean result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg");
        assertFalse(result);
    }
}
