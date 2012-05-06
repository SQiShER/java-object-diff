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

package de.danielbechler.diff.path;

/**
 * Serves mainly as marker class and enforces the proper implementation of hashCode(), equals() and toString() for all
 * property path elements.
 *
 * @author Daniel Bechler
 */
public abstract class Element
{
	/**
	 * Must be implemented in a way so that this element can be distinguished from the other ones.
	 *
	 * @param obj The object to check equality against.
	 *
	 * @return <code>true</code> is the given object equals this one, otherwise <code>false</code>.
	 */
	public abstract boolean equals(Object obj);

	/**
	 * Make sure to implement this properly. If two elements are equal, their hash code must be equal as well. However, it is
	 * absolutely okay if two unequal elements return the same hash code. A simple implementation could just return
	 * <code>0</code>.
	 *
	 * @return The hash code of this element.
	 */
	public abstract int hashCode();

	/**
	 * The string representation will only be used to print readable property paths for debug purposes.
	 *
	 * @return A string representation of this element for debug purposes.
	 */
	public abstract String toString();
}
