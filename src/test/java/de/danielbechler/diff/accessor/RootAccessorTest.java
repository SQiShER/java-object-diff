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

package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

/** @author Daniel Bechler */
public class RootAccessorTest
{
	private final Accessor accessor = new RootAccessor();

	@Test
	public void testGet() throws Exception
	{
		final Object root = new Object();
		Assert.assertThat(accessor.get(root), IsSame.sameInstance(root));
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testSet() throws Exception
	{
		final Object original = new Object();
		final Object replacement = new Object();
		accessor.set(original, replacement);
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testUnset() throws Exception
	{
		final Object original = new Object();
		accessor.unset(original);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.getPathElement(), Is.is(RootElement.class));
	}
}
