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
package com.github.secretx33.sccfg.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation can combine multiple {@link PathComment} into a single place.<br><br>
 *
 * You can use this annotation on the config class itself.<br>
 *
 * <pre><code>
 * &#64;Configuration
 * &#64;PathComments({
 *     &#64;PathComment(path = "some", comment = {"some comment"}),
 *     &#64;PathComment(path = "some.path", comment = {"some other comment"})
 * })
 * public MyConfigClass {
 *     // ...
 * }</code></pre><br>
 *
 * Or directly on a property that is not ignored.<br>
 *
 * <pre><code>
 * &#64;Configuration
 * public MyConfigClass {
 *
 *     &#64;PathComments({
 *        &#64;PathComment(path = "some", comment = {"some comment"}),
 *        &#64;PathComment(path = "some.path", comment = {"some other comment"})
 *     })
 *     public int someValue = 1;
 * }</code></pre>
 *
 * @see PathComment
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PathComments {

    PathComment[] value();
}
