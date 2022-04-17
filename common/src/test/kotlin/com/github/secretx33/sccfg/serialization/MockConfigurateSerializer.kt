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

import com.github.secretx33.sccfg.config.ConfigWrapper
import org.spongepowered.configurate.BasicConfigurationNode
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.ConfigurationOptions
import org.spongepowered.configurate.loader.AbstractConfigurationLoader
import org.spongepowered.configurate.loader.CommentHandler
import java.io.BufferedReader
import java.io.Writer
import java.util.logging.Logger

class MockConfigurateSerializer(logger: Logger, gsonProvider: GsonProvider) : AbstractConfigurateSerializer<MockBuilder, AbstractConfigurationLoader<BasicConfigurationNode>>(logger, gsonProvider) {
    override fun fileBuilder(configWrapper: ConfigWrapper<*>?): AbstractConfigurationLoader.Builder<MockBuilder, AbstractConfigurationLoader<BasicConfigurationNode>> = MockBuilder()
}

class MockBuilder : AbstractConfigurationLoader.Builder<MockBuilder, AbstractConfigurationLoader<BasicConfigurationNode>>() {
    override fun build(): AbstractConfigurationLoader<BasicConfigurationNode> = MockLoader(this, emptyArray())
}

private class MockLoader(builder: MockBuilder, commentHandlers: Array<CommentHandler>) : AbstractConfigurationLoader<BasicConfigurationNode>(builder, commentHandlers) {

    override fun createNode(options: ConfigurationOptions): BasicConfigurationNode = BasicConfigurationNode.root(options)

    override fun saveInternal(node: ConfigurationNode, writer: Writer) {}

    override fun loadInternal(node: BasicConfigurationNode, reader: BufferedReader) {}
}
