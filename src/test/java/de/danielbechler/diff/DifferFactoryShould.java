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

import de.danielbechler.diff.mock.*;
import org.mockito.Mock;
import org.testng.annotations.*;

import java.util.*;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class DifferFactoryShould
{
	private DifferFactory differFactory;
	@Mock
	private DifferDelegator differDelegator;
	@Mock
	private Configuration configuration;

	@BeforeMethod
	public void initDifferFactory()
	{
		initMocks(this);
		differFactory = new DifferFactory(configuration);
	}

	@Test(dataProvider = "primitiveTypes")
	public void return_primitive_differ_for_primitive_type(final Class<?> type)
	{
		final Differ<?> differ = differFactory.createDiffer(type, differDelegator);

		assertThat(differ).isInstanceOf(PrimitiveDiffer.class);
	}

	@Test(dataProvider = "collectionTypes")
	public void return_collection_differ_for_collection_type(final Class<? extends Collection<?>> type)
	{
		final Differ<?> differ = differFactory.createDiffer(type, differDelegator);

		assertThat(differ).isInstanceOf(CollectionDiffer.class);
	}

	@Test(dataProvider = "mapTypes")
	public void return_map_differ_for_map_types(final Class<? extends Map<?, ?>> type)
	{
		final Differ<?> differ = differFactory.createDiffer(type, differDelegator);

		assertThat(differ).isInstanceOf(MapDiffer.class);
	}

	@Test(dataProvider = "beanTypes")
	public void return_bean_differ_for_any_other_type(final Class<?> type)
	{
		final Differ<?> differ = differFactory.createDiffer(type, differDelegator);

		assertThat(differ).isInstanceOf(BeanDiffer.class);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_no_delegating_object_differ_was_given()
	{
		differFactory = new DifferFactory(configuration);
		differFactory.createDiffer(Object.class, null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_configuration()
	{
		new DifferFactory(null);
	}

	@DataProvider
	public static Object[][] collectionTypes()
	{
		return new Object[][] {
				new Object[] {Collection.class},
				new Object[] {List.class},
				new Object[] {Queue.class},
				new Object[] {Set.class},
				new Object[] {ArrayList.class},
				new Object[] {LinkedList.class},
		};
	}

	@DataProvider
	public static Object[][] mapTypes()
	{
		return new Object[][] {
				new Object[] {Map.class},
				new Object[] {HashMap.class},
				new Object[] {TreeMap.class},
		};
	}

	@DataProvider
	public static Object[][] beanTypes()
	{
		return new Object[][] {
				new Object[] {Object.class},
				new Object[] {ObjectWithString.class},
				new Object[] {Date.class},
		};
	}

	@DataProvider
	public Object[][] primitiveTypes()
	{
		return new Object[][] {
				new Object[] {int.class},
				new Object[] {short.class},
				new Object[] {char.class},
				new Object[] {long.class},
				new Object[] {boolean.class},
				new Object[] {byte.class},
				new Object[] {float.class},
				new Object[] {double.class},
		};
	}
}
