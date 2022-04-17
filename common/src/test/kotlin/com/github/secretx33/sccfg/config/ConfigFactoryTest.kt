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
package com.github.secretx33.sccfg.config

import com.github.secretx33.sccfg.scanner.ScannerImpl
import com.github.secretx33.sccfg.serialization.GsonProviderImpl
import com.github.secretx33.sccfg.serialization.MockConfigurateSerializer
import com.github.secretx33.sccfg.serialization.SerializerFactory
import com.github.secretx33.sccfg.serialization.namemapping.NameMapperFactory
import com.github.secretx33.sccfg.serialization.namemapping.mapper.NoneMapper
import com.github.secretx33.sccfg.storage.FileWatcher
import com.github.secretx33.sccfg.util.configexample.ConfigExample
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import kotlin.io.path.Path

class ConfigFactoryTest {

    private lateinit var fileWatcher: FileWatcher
    private lateinit var scanner: ScannerImpl
    private lateinit var serializerFactory: SerializerFactory
    private lateinit var factory: ConfigFactoryImpl

    @BeforeEach
    fun setup() {
        fileWatcher = mock {
            on { getWatcher(any()) } doReturn mock()
        }
        scanner = ScannerImpl("random.string", setOf(this::class.java.classLoader))
        val serializer = MockConfigurateSerializer(mock(), GsonProviderImpl(mock(), scanner))
        serializerFactory = mock {
            on { getSerializer(any()) } doReturn serializer
        }
        val nameMapperFactory = mock<NameMapperFactory> {
            on { getMapper(any()) } doReturn NoneMapper()
        }
        factory = ConfigFactoryImpl(Path(""), scanner, fileWatcher, mock(), mock(), serializerFactory, nameMapperFactory)
    }

    @Test
    fun `given correct config then getWrapper should wrap it`() {
        val wrapper = assertDoesNotThrow("Unable to wrap a valid config class") { factory.getWrapper(ConfigExample::class.java) }
        assert(wrapper is ConfigWrapperImpl)
    }
}
