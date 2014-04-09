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

package de.danielbechler.diff.node;

import de.danielbechler.diff.mock.ObjectWithIdentityAndValue;
import de.danielbechler.diff.node.path.CollectionItemElementSelector;
import de.danielbechler.diff.node.path.ElementSelector;
import org.fest.assertions.core.Condition;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Daniel Bechler
 */
public class CollectionItemAccessorShould
{
	private CollectionItemAccessor accessor;
	private ObjectWithIdentityAndValue referenceItem;
	private Collection<ObjectWithIdentityAndValue> collection;

	@BeforeMethod
	public void setUp() throws Exception
	{
		referenceItem = new ObjectWithIdentityAndValue("1", "foo");
		accessor = new CollectionItemAccessor(referenceItem);
		collection = new ArrayList<ObjectWithIdentityAndValue>();
	}

	@Test
	public void be_instantiatable()
	{
		accessor = new CollectionItemAccessor(referenceItem);

		assertThat(accessor).isNotNull();
	}

	@Test
	public void be_instantiatable_without_reference_item()
	{
		accessor = new CollectionItemAccessor(null);

		assertThat(accessor).isNotNull();
	}

	@Test
	public void retrieve_item_from_collection() throws Exception
	{
		collection.add(new ObjectWithIdentityAndValue("1"));

		final Object item = accessor.get(collection);

		assertThat(item).isEqualTo(referenceItem);
	}

	@Test
	public void retrieve_null_from_collection_if_it_does_not_contain_the_accessed_item() throws Exception
	{
		final Object item = accessor.get(collection);

		assertThat(item).isNull();
	}

	@Test
	public void retrieve_null_if_accessed_object_is_null() throws Exception
	{
		final Object item = accessor.get(null);

		assertThat(item).isNull();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_retrieve_if_target_object_is_not_a_collection() throws Exception
	{
		accessor.get(new Object());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_insert_if_target_object_is_not_a_collection() throws Exception
	{
		accessor.set(new Object(), null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_remove_if_target_object_is_not_a_collection() throws Exception
	{
		accessor.unset(new Object());
	}

	@Test
	public void return_proper_path_element()
	{
		final CollectionItemElementSelector expectedPathElement = new CollectionItemElementSelector(referenceItem);

		final ElementSelector actualPathElementSelector = accessor.getElementSelector();

		assertThat(actualPathElementSelector).isEqualTo(expectedPathElement);
	}

	@Test
	public void insert_item_into_collection() throws Exception
	{
		assertThat(collection).isEmpty();

		accessor.set(collection, referenceItem);

		assertThat(collection).containsOnly(referenceItem);
	}

	@Test
	public void insert_nothing_if_target_object_is_null() throws Exception
	{
		accessor.set(null, referenceItem);
	}

	@Test
	public void replace_same_item_in_list() throws Exception
	{
		collection.add(new ObjectWithIdentityAndValue("1", "foo"));

		accessor.set(collection, new ObjectWithIdentityAndValue("1", "bar"));

		assertThat(collection).hasSize(1);
		assertThat(collection.iterator().next().getValue()).isEqualTo("bar");
	}

	@Test
	public void replace_same_item_in_set() throws Exception
	{
		collection = new HashSet<ObjectWithIdentityAndValue>();
		collection.add(new ObjectWithIdentityAndValue("1", "foo"));

		accessor.set(collection, new ObjectWithIdentityAndValue("1", "bar"));

		assertThat(collection).hasSize(1);
		assertThat(collection.iterator().next().getValue()).isEqualTo("bar");
	}

	@Test
	public void remove_item_from_collection()
	{
		collection.add(referenceItem);

		accessor.unset(collection);

		assertThat(collection).isEmpty();
	}

	@Test
	public void not_fail_if_item_to_remove_is_not_in_collection()
	{
		accessor.unset(collection);

		assertThat(collection).isEmpty();
	}

	@Test
	public void return_type_of_reference_item() throws Exception
	{
		assertThat(accessor.getType()).is(new SameTypeAs(ObjectWithIdentityAndValue.class));
	}

	@Test
	public void return_null_type_if_reference_item_is_null()
	{
		accessor = new CollectionItemAccessor(null);

		assertThat(accessor.getType()).isNull();
	}

	@Test
	public void mention_its_type_in_string_representation() throws Exception
	{
		assertThat(accessor.toString()).startsWith("collection item ");
	}

	private static class SameTypeAs extends Condition<Class<?>>
	{
		private final Class<?> type;

		public SameTypeAs(final Class<?> type)
		{
			super(type.getCanonicalName());
			this.type = type;
		}

		@Override
		public boolean matches(final Class<?> value)
		{
			return value == type;
		}
	}
}
