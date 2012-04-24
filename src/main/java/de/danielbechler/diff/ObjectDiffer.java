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

package de.danielbechler.diff;

import de.danielbechler.diff.node.*;

/** @author Daniel Bechler */
public interface ObjectDiffer extends Configurable
{
	/**
	 * Recursively inspects the given objects and returns a node representing their differences. Both objects have be have the
	 * same type.
	 *
	 * @param working This object will be treated as the successor of the <code>base</code> object.
	 * @param base	This object will be treated as the predecessor of the <code>working</code> object.
	 * @param <T>     The type of the objects to compare.
	 *
	 * @return A node representing the differences between the given objects.
	 */
	<T> Node compare(T working, T base);
}
