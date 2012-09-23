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
import org.testng.annotations.*;

import java.util.*;

import static org.fest.assertions.api.Assertions.*;

/** @author Daniel Bechler */
public class InstancesTest
{
	@Test(expectedExceptions = IllegalStateException.class)
	public void testGetType_throws_exception_for_incompatible_types() throws Exception
	{
		new Instances(RootAccessor.getInstance(), "foo", 1, null).getType();
	}

	@Test
	public void testGetType_returns_Collection_type_for_different_Collection_implementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new ArrayList<Object>(), new LinkedHashSet(), null);
		final Class<?> type = instances.getType();
		assertThat(type == Collection.class);
	}

	@Test
	public void testGetType_returns_Map_type_for_different_Map_implementations() throws Exception
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), new HashMap<Object, Object>(), new TreeMap<Object, Object>(), null);
		final Class<?> type = instances.getType();
		assertThat(type == Map.class);
	}

	@Test
	public void testAreNull_returns_true_when_base_and_working_are_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), null, null, null);
		assertThat(instances.areNull()).isTrue();
	}

	@Test
	public void testAreNull_returns_false_when_base_is_not_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), null, "", null);
		assertThat(instances.areNull()).isFalse();
	}

	@Test
	public void testAreNull_returns_false_when_working_is_not_null()
	{
		final Instances instances = new Instances(RootAccessor.getInstance(), "", null, null);
		assertThat(instances.areNull()).isFalse();
	}

	@Test
	public void testArePrimitiveReturnsTrueForPrimitiveType()
	{
		assertThat(new Instances(RootAccessor.getInstance(), 1L, 2L, 0L).arePrimitive()).isTrue();
	}

	@Test
	public void testArePrimitiveReturnsFalseForComplexType()
	{
		assertThat(new Instances(RootAccessor.getInstance(), "1", "2", null).arePrimitive()).isFalse();
	}
}
