package com.github.secretx33.sccfg.serialization

import com.github.secretx33.sccfg.api.FileType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.mockito.kotlin.mock
import kotlin.test.assertNotNull

class SerializerFactoryTest {

    private lateinit var factory: SerializerFactory

    @BeforeEach
    fun setup() {
        factory = SerializerFactory(mock(), mock())
    }

    @ParameterizedTest
    @EnumSource(FileType::class)
    fun `given any file type then always return a valid serializer`(type: FileType) {
        val serializer = assertDoesNotThrow({ "missing serializer for type $type! "}) {
            factory.getFor(type)
        }
        assertNotNull(serializer) { "serializer should never come null" }
    }
}
