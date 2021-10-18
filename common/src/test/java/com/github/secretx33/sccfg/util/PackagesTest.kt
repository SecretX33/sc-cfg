package com.github.secretx33.sccfg.util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PackagesTest {

    private lateinit var mockedPackage: Package

    @BeforeEach
    fun setup() {
        mockedPackage = mock()
    }

    @Test
    fun givenPackage_whenIsTheEqualToTheOtherPackage_thenReturnTrue() {
        whenever(mockedPackage.name) doReturn "com.github.secretx33.sccfg"
        val result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg")
        assertTrue(result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["com.github.secretx33.sccfg.anotherpackage",
        "com.github.secretx33.sccfg.serialization.typeadapters",
        "com.github.secretx33.sccfg.yes.no.yes",
        "com.github.secretx33.sccfg.this.should.be.a.very.long.package"])
    fun `given package when is deeper related to other package then return true`() {
        whenever(mockedPackage.name) doReturn "com.github.secretx33.sccfg.serialization.typeadapters"
        val result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg")
        assertTrue(result)
    }

    @Test
    fun `given package when is within higher package then return false`() {
        whenever(mockedPackage.name) doReturn "com.github.secretx33"
        val result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg")
        assertFalse(result)
    }

    @ParameterizedTest
    @ValueSource(strings = ["", "   ", "com.github.secretx33.anothersccfg",
        "com.github.secretx33.anothersccfg.serialization.typeadapters",
        "me.anotherperson.coolproject"])
    fun `given package when not within other package then return false`(pkg: String) {
        whenever(mockedPackage.name) doReturn pkg
        val result = Packages.isPackageWithin(mockedPackage, "com.github.secretx33.sccfg")
        assertFalse(result)
    }
}
