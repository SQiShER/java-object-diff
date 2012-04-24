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

import java.util.*;

/** @author Daniel Bechler */
public class MapEntryAccessorTest
{
	private MapEntryAccessor accessor;

	@Before
	public void setUp()
	{
		accessor = new MapEntryAccessor(Arrays.asList("a", "b", "c"), 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConstructionWithInvalidIndex()
	{
		new MapEntryAccessor(Arrays.asList("foo"), 5);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.getPathElement(), Is.is(MapElement.class));
	}

	@Test
	public void testSet() throws Exception
	{
		final TreeMap<String, String> map = new TreeMap<String, String>();

		accessor.set(map, "foo");
		Assert.assertThat(map.get("b"), IsEqual.equalTo("foo"));

		accessor.set(map, "bar");
		Assert.assertThat(map.get("b"), IsEqual.equalTo("bar"));
	}

	@Test
	public void testGetFromMap() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		Assert.assertThat((String) accessor.get(map), IsEqual.equalTo("foo"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFromInvalidObjectType()
	{
		accessor.get(new Object());
	}

	@Test
	public void testGetFromNull()
	{
		Assert.assertThat(accessor.get(null), IsNull.nullValue());
	}

	@Test
	public void testUnset() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		accessor.unset(map);
		Assert.assertTrue(map.isEmpty());
	}
}
