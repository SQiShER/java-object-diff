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
import org.testng.annotations.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsInstanceOf.*;

/** @author Daniel Bechler */
public class MapEntryAccessorTest
{
	private MapEntryAccessor accessor;

	@BeforeMethod
	public void setUp()
	{
		accessor = new MapEntryAccessor(Arrays.asList("a", "b", "c"), 1);
	}

	@Test(expectedExceptions = IndexOutOfBoundsException.class)
	public void testConstructionWithInvalidIndex()
	{
		new MapEntryAccessor(Arrays.asList("foo"), 5);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		final Element pathElement = accessor.getPathElement();
		assertThat(pathElement, is(instanceOf(MapElement.class)));
	}

	@Test
	public void testSet() throws Exception
	{
		final TreeMap<String, String> map = new TreeMap<String, String>();

		accessor.set(map, "foo");
		assertThat(map.get("b"), equalTo("foo"));

		accessor.set(map, "bar");
		assertThat(map.get("b"), equalTo("bar"));
	}

	@Test
	public void testGetFromMap() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		assertThat((String) accessor.get(map), equalTo("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetFromInvalidObjectType()
	{
		accessor.get(new Object());
	}

	@Test
	public void testGetFromNull()
	{
		assertThat(accessor.get(null), IsNull.nullValue());
	}

	@Test
	public void testUnset() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		accessor.unset(map);
		org.testng.Assert.assertTrue(map.isEmpty());
	}
}
