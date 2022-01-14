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
package com.github.secretx33.sccfg.serialization.namemapping.mapper;

import com.github.secretx33.sccfg.api.Naming;
import com.github.secretx33.sccfg.serialization.namemapping.NameMapper;
import com.github.secretx33.sccfg.util.Predicates;
import org.intellij.lang.annotations.Language;

import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.github.secretx33.sccfg.util.Preconditions.checkNotBlank;
import static com.github.secretx33.sccfg.util.Preconditions.checkNotNull;

abstract class AbstractNameMapper implements NameMapper {

    protected final Naming nameStrategy;

    public AbstractNameMapper(final Naming nameStrategy) {
        this.nameStrategy = checkNotNull(nameStrategy, "nameStrategy");
    }

    @Override
    public String applyStrategy(final String string) {
        checkNotBlank(string, "name");

        final String[] chars = string.codePoints()
                .mapToObj(cp -> new String(Character.toChars(cp)))
                .toArray(String[]::new);

        final boolean hasUppercaseLetter = hasUppercase(chars);
        final boolean hasUnderline = hasUnderline(chars);
        final NameScheme nameScheme = getNameScheme(hasUppercaseLetter, hasUnderline);

        return applyNameStrategy(string, nameScheme);
    }

    abstract String applyNameStrategy(final String string, final NameScheme nameScheme);

    private NameScheme getNameScheme(final boolean hasUppercase, final boolean hasUnderline) {
        if (hasUppercase && hasUnderline) return NameScheme.UPPERCASE_SNAKE_CASE;
        if (hasUnderline) return NameScheme.SNAKE_CASE;
        return NameScheme.CAMEL_CASE;
    }

    private boolean hasUppercase(final String[] chars) {
        return Predicates.any(chars, letter -> !letter.equals(letter.toLowerCase(Locale.US)));
    }

    private boolean hasUnderline(final String[] chars) {
        return Predicates.any(chars, letter -> letter.equals("_"));
    }

    protected String replaceAll(
            final String text,
            @Language("regexp") final String regex,
            final String replace
    ) {
        return replaceAll(text, regex, replace, Function.identity());
    }

    protected static String replaceAll(
            final String text,
            @Language("regexp") final String regex,
            final Function<String, String> transformer
    ) {
        return replaceAll(text, regex, "$0", transformer);
    }

    protected static String replaceAll(
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
}
