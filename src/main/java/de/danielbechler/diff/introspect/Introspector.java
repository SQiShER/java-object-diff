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

package de.danielbechler.diff.introspect;

import de.danielbechler.diff.accessor.*;

/**
 * Resolves the accessors of a given type.
 *
 * @author Daniel Bechler
 */
public interface Introspector
{
	/**
	 * Resolves the accessors of the given type.
	 *
	 * @param type The type to introspect.
	 *
	 * @return All valid accessors.
	 */
	Iterable<Accessor> introspect(Class<?> type);
}
