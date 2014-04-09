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

package de.danielbechler.diff.config.introspection;

import de.danielbechler.diff.mock.ObjectWithMap;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.singletonMap;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.data.MapEntry.entry;

/**
 * @author Daniel Bechler
 */
public class BeanPropertyAccessorForMapsShould
{
	private BeanPropertyAccessor accessor;
	private Map<String, String> targetMap;
	private ObjectWithMap target;

	@BeforeMethod
	public void setUp() throws NoSuchMethodException
	{
		targetMap = new LinkedHashMap<String, String>();
		target = new ObjectWithMap();
		target.setMap(targetMap);
		accessor = createMapPropertyAccessor(true);
	}

	private static BeanPropertyAccessor createMapPropertyAccessor(final boolean readOnly) throws NoSuchMethodException
	{
		final Method readMethod = ObjectWithMap.class.getMethod("getMap");
		final Method writeMethod;
		if (readOnly)
		{
			writeMethod = null;
		}
		else
		{
			writeMethod = ObjectWithMap.class.getMethod("setMap", Map.class);
		}
		return new BeanPropertyAccessor("map", readMethod, writeMethod);
	}

	@Test
	public void assign_by_replacing_existing_content_if_no_write_method_is_available() throws Exception
	{
		accessor.set(target, singletonMap("foo", "bar"));

		assertThat(targetMap).contains(entry("foo", "bar"));
	}

	@Test
	public void assign_nothing_if_target_map_is_immutable() throws Exception
	{
		targetMap = Collections.emptyMap();
		target.setMap(targetMap);

		accessor.set(target, singletonMap("foo", "bar"));

		assertThat(targetMap).isEmpty();
	}

	@Test
	public void assign_nothing_if_target_map_is_null_and_no_write_method_is_available() throws Exception
	{
		target.setMap(null);

		accessor.set(target, singletonMap("foo", "bar"));

		assertThat(target.getMap()).isNull();
	}
}
