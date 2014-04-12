/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.danielbechler.diff.introspection;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Daniel Bechler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ObjectDiffEqualsOnlyType
{
	/**
	 * The name of a method of the annotated type. The value returned by this method will be used for the equals
	 * check instead of the annotated object itself. This allows to provide alternative equality checks (e.g.
	 * comparing a List type by size, by calling <code>size()</code>.) The method should have no parameters.
	 *
	 * @return The name of a method providing a different object for the equals check.
	 */
	String valueProviderMethod() default "";
}
