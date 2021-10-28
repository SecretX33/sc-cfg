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
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.wrapper.ConfigWrapper;
import com.github.secretx33.sccfg.exception.ConfigDeserializationException;
import com.github.secretx33.sccfg.exception.ConfigException;
import com.github.secretx33.sccfg.exception.ConfigSerializationException;
import com.github.secretx33.sccfg.serialization.gson.GsonFactory;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class JsonSerializer extends AbstractSerializer {

    public JsonSerializer(final Logger logger, final GsonFactory gsonFactory) {
        super(logger, gsonFactory);
    }

    @Override
    protected Map<String, Object> loadFromFile(ConfigWrapper<?> configWrapper) {
        final Path path = configWrapper.getDestination();
        final Gson gson = gsonFactory.getPrettyPrintInstance();
        final String file;

        try {
            file = String.join("", Files.readAllLines(path, StandardCharsets.UTF_8));
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An IO error has occurred while reading your config file '" + configWrapper.getDestination().getFileName() + "' from the disk.", e);
            return Collections.emptyMap();
        }

        try {
            return gson.fromJson(file, linkedMapToken);
        } catch (final JsonParseException e) {
            logger.log(Level.SEVERE, "An error has occurred when deserializing file '" + configWrapper.getDestination().getFileName() + "' from " + configWrapper.getFileType() + ". There is probably some kind of typo on it, so it could not be parsed, please fix any typos on the file.", new ConfigDeserializationException(e));
        }
        return Collections.emptyMap();
    }

    @Override
    protected void saveToFile(ConfigWrapper<?> configWrapper, Map<String, Object> newValues) {
        final Path path = configWrapper.getDestination();
        final Gson gson = gsonFactory.getPrettyPrintInstance();
        final String file;

        try {
            file = gson.toJson(newValues, linkedMapToken);
        } catch (final Exception e) {
            final ConfigSerializationException ex = new ConfigSerializationException(e);
            logger.log(Level.SEVERE, "An error has occurred when serializing config class " + configWrapper.getInstance().getClass().getName(), ex);
            throw ex;
        }

        try (final Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(file);
        } catch (final IOException e) {
            logger.log(Level.SEVERE, "An error has occurred when saving your config file '" + configWrapper.getDestination().getFileName() + " to the disk.", e);
            throw new ConfigException(e);
        }
    }
}
