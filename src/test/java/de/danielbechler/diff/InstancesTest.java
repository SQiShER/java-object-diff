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

package de.danielbechler.diff;

import de.danielbechler.diff.accessor.*;
import org.fest.assertions.api.*;
import org.junit.*;

import java.util.*;

/** @author Daniel Bechler */
public class InstancesTest
{
	@Test(expected = IllegalStateException.class)
	public void testGetType_throws_exception_for_incompatible_types() throws Exception
	{
		new Instances(RootAccessor.getInstance(), "foo", 1, null).getType();
	}

	@Test
	public void testGetType_returns_Collection_type_for_different_Collection_implementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new ArrayList<Object>(), new LinkedHashSet(), null);
		final Class<?> type = instances.getType();
		Assertions.assertThat(type == Collection.class);
	}

	@Test
	public void testGetType_returns_Map_type_for_different_Map_implementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new HashMap<Object, Object>(), new TreeMap<Object, Object>(), null);
		final Class<?> type = instances.getType();
		Assertions.assertThat(type == Map.class);
	}
}
