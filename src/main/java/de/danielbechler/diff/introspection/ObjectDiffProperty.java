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

import de.danielbechler.diff.inclusion.Inclusion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to be used on property getters in order to configure if and how they should be treated during
 * object comparison.
 *
 * @author Daniel Bechler
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ObjectDiffProperty
{
	/**
	 * Causes the {@link de.danielbechler.diff.differ.Differ Differs} to skip the marked property and all its children.
	 *
	 * @return <code>true</code> if the property should be ignored.
	 * @deprecated Please use {@linkplain #inclusion()} instead. When used in conjunction with {@linkplain
	 * #inclusion()}, the latter one will win over {@linkplain #excluded()}.
	 */
	@Deprecated
	public boolean excluded() default false;

	public Inclusion inclusion() default Inclusion.DEFAULT;

	/**
	 * Causes the {@link de.danielbechler.diff.differ.Differ Differs} to compare the object by using the {@link
	 * Object#equals(Object)} method instead of introspection.
	 *
	 * @return <code>true</code> if the property should be compared via {@link Object#equals(Object)}.
	 */
	public boolean equalsOnly() default false;

	/**
	 * Categories will be passed along with the object node and can be used for advanced filtering of specific
	 * property groups.
	 *
	 * @return The categories for this property.
	 */
	public String[] categories() default {};

	/**
	 * Can be used in conjunction with {@link #equalsOnly()} to name a method on the object that provides the
	 * value to compare via equals.
	 *
	 * @return The method returning the object to use for the equals check.
	 */
	public String equalsOnlyValueProviderMethod() default "";
}
