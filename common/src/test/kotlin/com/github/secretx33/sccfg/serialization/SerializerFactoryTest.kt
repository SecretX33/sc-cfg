/*
 * Copyright (C) 2021-2022 SecretX <notyetmidnight@gmail.com>
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
            factory.getSerializer(type)
        }
        assertNotNull(serializer) { "serializer should never come null" }
    }
}
