/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
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
