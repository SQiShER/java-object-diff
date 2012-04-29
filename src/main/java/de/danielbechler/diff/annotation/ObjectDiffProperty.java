/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff.annotation;

import java.lang.annotation.*;

/**
 * Annotation to be used on property getters in order to configure if and how they should be treated during object
 * comparison.
 *
 * @author Daniel Bechler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@ObjectDiffAnnotation
public @interface ObjectDiffProperty
{
	/**
	 * Causes the {@link de.danielbechler.diff.Differ Differs} to skip the marked property and all its children.
	 *
	 * @return <code>true</code> if the property should be ignored.
	 */
	public boolean ignore() default false;

	/**
	 * Causes the {@link de.danielbechler.diff.Differ Differs} to compare the object by using the {@link Object#equals(Object)}
	 * method instead of introspection.
	 *
	 * @return <code>true</code> if the property should be compared via {@link Object#equals(Object)}.
	 */
	public boolean equalsOnly() default false;

	/**
	 * Categories will be passed along with the object node and can be used for advanced filtering of specific property groups.
	 *
	 * @return The categories for this property.
	 */
	public String[] categories() default {};
}
