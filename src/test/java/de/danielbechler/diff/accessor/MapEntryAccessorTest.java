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
