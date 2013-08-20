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

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.testng.annotations.*;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsInstanceOf.*;

/** @author Daniel Bechler */
public class MapEntryAccessorShould
{
	private MapEntryAccessor accessor;

	@BeforeMethod
	public void setUp()
	{
		accessor = new MapEntryAccessor("b");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_on_construction_with_null_referecneKey()
	{
		new MapEntryAccessor(null);
	}

	@Test
	public void provide_access_to_its_path_element() throws Exception
	{
		final Element pathElement = accessor.getPathElement();
		assertThat(pathElement, is(instanceOf(MapElement.class)));
	}

	@Test
	public void provide_write_access_to_referenced_value_in_any_map() throws Exception
	{
		final TreeMap<String, String> map = new TreeMap<String, String>();

		accessor.set(map, "foo");
		assertThat(map.get("b"), equalTo("foo"));

		accessor.set(map, "bar");
		assertThat(map.get("b"), equalTo("bar"));
	}

	@Test
	public void provide_read_access_to_referenced_value_in_any_map() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		assertThat((String) accessor.get(map), equalTo("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void throw_exception_when_trying_to_read_from_non_map_object()
	{
		accessor.get(new Object());
	}

	@Test
	public void return_null_when_reading_from_null_object()
	{
		assertThat(accessor.get(null), IsNull.nullValue());
	}

	@Test
	public void remove_referenced_entry_from_any_map() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		accessor.unset(map);
		org.testng.Assert.assertTrue(map.isEmpty());
	}

	@Test
	public void return_the_key_object_of_the_given_map()
	{
		final ObjectWithIdentityAndValue referenceKey = new ObjectWithIdentityAndValue("key", "1");
		accessor = new MapEntryAccessor(referenceKey);

		final Map<ObjectWithIdentityAndValue, String> map = new HashMap<ObjectWithIdentityAndValue, String>();
		final ObjectWithIdentityAndValue expectedKey = new ObjectWithIdentityAndValue("key", "2");
		map.put(expectedKey, "foo");

		final ObjectWithIdentityAndValue key = (ObjectWithIdentityAndValue) accessor.getKey(map);

		assertThat(key).isSameAs(expectedKey);
		assertThat(key).isNotSameAs(referenceKey);
	}

	@Test
	public void return_null_as_the_key_object_if_the_target_object_is_null()
	{
		final Object key = accessor.getKey(null);

		assertThat(key).isNull();
	}

	@Test
	public void return_null_as_the_key_object_if_the_given_map_does_not_contain_it()
	{
		final Map<String, String> map = new HashMap<String, String>();
		map.put("d", "whatever value");
		assertThat(accessor.getKey(map)).isNull();
	}
}
