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
package com.github.secretx33.sccfg.serialization.namemapping

import com.github.secretx33.sccfg.api.Naming
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertNotNull

class NameMapperFactoryTest {

    private lateinit var factory: NameMapperFactory

    @BeforeEach
    fun setup() {
        factory = NameMapperFactory()
    }

    @ParameterizedTest
    @EnumSource(Naming::class)
    fun `given any file type then always return a valid serializer`(naming: Naming) {
        val serializer = assertDoesNotThrow({ "missing name mapper for naming $naming! "}) {
            factory.getMapper(naming)
        }
        assertNotNull(serializer) { "name mapper should never come null" }
    }
}
