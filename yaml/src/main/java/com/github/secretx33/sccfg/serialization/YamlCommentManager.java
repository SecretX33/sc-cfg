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
package com.github.secretx33.sccfg.serialization;

import com.github.secretx33.sccfg.config.ConfigWrapper;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

/**
 * This class is responsible for adding comments specified by a {@link ConfigWrapper} to a YAML file because
 * the underlying library, Configurate, does not have comment support for YAML files.
 */
final class YamlCommentManager {

    private static final String COMMENT_PREFIX = "# ";
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(\\s*?#.*)$");
    private static final Pattern KEY_PATTERN = Pattern.compile("^\\s*([\\w\\d\\-!@#$%^&*+]+?):.*$");
    private static final Pattern LIST_PATTERN = Pattern.compile("^\\s*-\\s?[\"']?(.+)[\"']?.*$");
    private static final Pattern DEPTH_PATTERN = Pattern.compile("^(\\s+)[^\\s]+");
    private static final Charset UTF8_CHARSET = StandardCharsets.UTF_8;

    private final Path file;
    private final Map<String, String[]> comments;

    YamlCommentManager(final ConfigWrapper<?> configWrapper) {
        checkNotNull(configWrapper, "configWrapper");
        this.file = configWrapper.getDestination();
        this.comments = configWrapper.getComments();
    }

    /**
     * Save the {@link YamlCommentManager#comments} extracted from the {@link ConfigWrapper} to the
     * {@link YamlCommentManager#file}.
     */
    public void saveComments() throws IOException {
        if (comments.isEmpty()) return;
        final List<String> lines = Files.readAllLines(file, UTF8_CHARSET);

        for (final Map.Entry<String, String[]> stringEntry : comments.entrySet()) {
            final String key = stringEntry.getKey();
            final String[] comment = stringEntry.getValue();
            final int index = findKey(lines, key);
            if (index == -1) continue;
            final int lineDepth = lineDepth(lines.get(index));
            lines.addAll(index, toYamlComment(comment, lineDepth));
        }
        Files.write(file, lines, UTF8_CHARSET);
    }

    private int findKey(final List<String> lines, final String key) {
        for (int i = 0; i < lines.size(); i++) {
            if (getFullKeyOfLine(i, lines).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    private String getFullKeyOfLine(final int index, final List<String> lines) {
        if (index < 0 || index >= lines.size()) return "";

        final String line = lines.get(index);
        final StringBuilder key = new StringBuilder();
        final int depth = lineDepth(line);
        boolean keyIsFromList = false;

        // line is an entry key
        String matchRes = matchOrNull(KEY_PATTERN, line, 1);
        if (matchRes == null) {
            // line is a list key
            matchRes = matchOrNull(LIST_PATTERN, line, 1);
            if (matchRes != null) {
                matchRes = matchRes.trim();
                keyIsFromList = true;
            }
        }
        if (matchRes == null) return "";
        key.append(matchRes);
        if (depth <= 0 || index == 0) return key.toString();

        for (int i = index - 1; i >= 0; i--) {
            final String currentLine = lines.get(i);
            if (isComment(currentLine)) continue;
            final int subDepth = lineDepth(currentLine);
            // if subKey has less depth than the original key, it means that this key is its parent
            // the list part is here because yaml is cancer and align the list entries with its parent key
            if (subDepth < depth || keyIsFromList && depth == subDepth && matches(KEY_PATTERN, currentLine)) {
                key.insert(0, '.');
                return key.insert(0, getFullKeyOfLine(i, lines)).toString();
            }
        }
        return key.toString();
    }

    private int lineDepth(final String line) {
        final Matcher match = DEPTH_PATTERN.matcher(line);
        return match.find() ? match.group(1).length() / YamlSerializer.SPACES_PER_DEPTH : 0;
    }

    @Nullable
    private String matchOrNull(final Pattern pattern, final String line, final int index) {
        final Matcher match = pattern.matcher(line);
        return match.matches() ? match.group(index) : null;
    }

    private boolean isComment(final String line) {
        return line.trim().isEmpty() || matches(COMMENT_PATTERN, line);
    }

    private boolean matches(final Pattern pattern, final String line) {
        return pattern.matcher(line).matches();
    }

    private List<String> toYamlComment(final String[] comment, final int depth) {
        final List<String> lines = new ArrayList<>(comment.length);
        boolean listContainsText = false;
        for (final String line : comment) {
            final StringBuilder sb = new StringBuilder();
            final boolean isLineBlank = line.trim().isEmpty();
            if (listContainsText || !isLineBlank) {
                for (int i = 0; i < depth * YamlSerializer.SPACES_PER_DEPTH; i++) {
                    sb.append(' ');
                }
                sb.append(COMMENT_PREFIX);
            }
            sb.append(line);
            if (!isLineBlank) listContainsText = true;
            lines.add(sb.toString());
        }
        return lines;
    }
}
