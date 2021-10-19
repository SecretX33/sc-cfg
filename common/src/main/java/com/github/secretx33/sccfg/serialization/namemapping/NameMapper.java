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
package com.github.secretx33.sccfg.serialization.namemapping;

import com.github.secretx33.sccfg.api.NameStrategy;
import com.github.secretx33.sccfg.util.Maps;
import com.github.secretx33.sccfg.util.Sets;
import org.intellij.lang.annotations.Language;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

public final class NameMapper {

    public NameMap mapFieldNamesUsing(final Set<Field> fields, final NameStrategy nameStrategy) {
        final Set<String> names = fields.stream().sequential()
                .map(Field::getName)
                .collect(Sets.toImmutableLinkedSet());
        return mapNamesUsing(names, nameStrategy);
    }

    public NameMap mapNamesUsing(final Set<String> names, final NameStrategy nameStrategy) {
        checkNotNull(nameStrategy, "nameStrategy");
        final Map<String, String> javaToFileNames = new HashMap<>(names.size());
        final Map<String, String> fileToJavaNames = new HashMap<>(names.size());

        names.forEach(javaName -> {
            final String fileName = mapNameUsingStrategy(javaName, nameStrategy);
            javaToFileNames.put(javaName, fileName);
            fileToJavaNames.put(fileName, javaName);
        });
        return new NameMap(Maps.immutableOf(javaToFileNames), Maps.immutableOf(fileToJavaNames));
    }

    private String mapNameUsingStrategy(final String name, final NameStrategy nameStrategy) {
        checkNotNull(name, "name");

        if (nameStrategy == NameStrategy.NONE) {
            return name;
        }

        final String[] chars = name.codePoints()
            .mapToObj(cp -> new String(Character.toChars(cp)))
            .toArray(String[]::new);

        final boolean hasUppercaseLetter = hasUppercase(chars);
        final boolean hasUnderline = hasUnderline(chars);
        final NameScheme nameScheme = getNameScheme(hasUppercaseLetter, hasUnderline);

        return applyNameStrategy(name, nameScheme, nameStrategy);
    }

    private NameScheme getNameScheme(final boolean hasUppercase, final boolean hasUnderline) {
        if (hasUppercase && hasUnderline) return NameScheme.UPPERCASE_SNAKE_CASE;
        if (hasUnderline) return NameScheme.SNAKE_CASE;
        return NameScheme.CAMEL_CASE;
    }

    private String applyNameStrategy(final String name, final NameScheme nameScheme, final NameStrategy nameStrategy) {
        if (nameStrategy == NameStrategy.LOWERCASE_HYPHENATED) {
            switch (nameScheme) {
                case CAMEL_CASE:
                    return replaceAll(name, "[A-Z]", "-$0").toLowerCase(Locale.US);
                case SNAKE_CASE:
                    return name.replace('_', '-');
                case UPPERCASE_SNAKE_CASE:
                    return name.toLowerCase(Locale.US).replace('_', '-');
                default:
                    throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
            }
        }

        if (nameStrategy == NameStrategy.UPPERCASE_HYPHENATED) {
            switch (nameScheme) {
                case CAMEL_CASE:
                    return replaceAll(name, "[A-Z]", "-$0").toUpperCase(Locale.US);
                case SNAKE_CASE:
                case UPPERCASE_SNAKE_CASE:
                    return name.toUpperCase(Locale.US).replace('_', '-');
                default:
                    throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
            }
        }

        if (nameStrategy == NameStrategy.LOWERCASE_UNDERLINED) {
            switch (nameScheme) {
                case CAMEL_CASE:
                    return replaceAll(name, "[A-Z]", "_$0").toLowerCase(Locale.US);
                case SNAKE_CASE:
                    return name;
                case UPPERCASE_SNAKE_CASE:
                    return name.toLowerCase(Locale.US);
                default:
                    throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
            }
        }

        if (nameStrategy == NameStrategy.UPPERCASE_UNDERLINED) {
            switch (nameScheme) {
                case CAMEL_CASE:
                    return replaceAll(name, "[A-Z]", "_$0").toUpperCase(Locale.US);
                case SNAKE_CASE:
                case UPPERCASE_SNAKE_CASE:
                    return name.toUpperCase(Locale.US);
                default:
                    throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
            }
        }

        throw new IllegalStateException("Missing conversion from '" + nameScheme + "' to nameStrategy '" + nameStrategy + "'");
    }

    private boolean hasUppercase(final String[] chars) {
        for (final String letter : chars) {
            if (!letter.equals(letter.toLowerCase(Locale.US))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasUnderline(final String[] chars) {
        for (final String letter : chars) {
            if (letter.equals("_")) {
                return true;
            }
        }
        return false;
    }

    private static String replaceAll(
        final String text,
        @Language("regexp") final String regex,
        final String replace
    ) {
        return replaceAll(text, regex, replace, Function.identity());
    }

    private static String replaceAll(
        final String text,
        @Language("regexp") final String regex,
        final Function<String, String> transformer
    ) {
        return replaceAll(text, regex, "$0", transformer);
    }

    private static String replaceAll(
        final String text,
        @Language("regexp") final String regex,
        final String replace,
        final Function<String, String> transformer
    ) {
        final StringBuffer sb = new StringBuffer(text.length());
        final Matcher matcher = Pattern.compile(regex).matcher(text);
        while (matcher.find()) {
            matcher.appendReplacement(sb, transformer.apply(matcher.group().replaceAll(regex, replace)));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private enum NameScheme {
        CAMEL_CASE,  // camelCase
        SNAKE_CASE,  // snake_case
        UPPERCASE_SNAKE_CASE;  // SNAKE_CASE
    }
}
