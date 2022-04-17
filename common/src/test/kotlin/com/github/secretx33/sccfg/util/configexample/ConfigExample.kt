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
package com.github.secretx33.sccfg.util.configexample

import com.github.secretx33.sccfg.api.annotation.Configuration

@Configuration
class ConfigExample {
    val monsterName = "Creeper"
    var monsterHealth = 10
    private val monsterDamage = 2
    var monsterSpeed = 0.25

    val attributes = mapOf(
        "strength" to 8,
        "dexterity" to 16,
        "constitution" to 10,
        "intelligence" to -10,
        "wisdom" to 5.5,
        "charisma" to -1.5,
    )

    private val databaseUrl = "https://example.com/database"
    private val databasePort = 3306
    private val databaseUsername = "root"
    private val databasePassword = "password"

    val contextList = mutableListOf(
        "minecraft:overworld",
        "minecraft:the_nether",
        "minecraft:the_end",
    )

    private var favoriteNumbers = listOf(5, 22, 7, 9, 1, 0)
    val uniquePersons = setOf("First Person", "Second Person", "Third Person")
}
