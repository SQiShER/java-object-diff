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

/**
 * Creates new {@link ObjectDiffer} instances.
 *
 * @author Daniel Bechler
 */
@SuppressWarnings({"UtilityClassWithoutPrivateConstructor"})
public final class ObjectDifferFactory
{
	ObjectDifferFactory()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns a new {@link ObjectDiffer} instance.
	 *
	 * @return A new {@link ObjectDiffer} instance.
	 */
	public static ObjectDiffer getInstance()
	{
		return new DelegatingObjectDifferImpl();
	}

	/**
	 * Returns a new {@link ObjectDiffer} instance and passes it the given {@link Configuration}
	 *
	 * @param configuration The configuration for the {@link ObjectDiffer}.
	 *
	 * @return A new {@link ObjectDiffer} instance.
	 */
	public static ObjectDiffer getInstance(final Configuration configuration)
	{
		final DelegatingObjectDifferImpl objectDiffer = new DelegatingObjectDifferImpl();
		objectDiffer.setConfiguration(configuration);
		return objectDiffer;
	}
}
