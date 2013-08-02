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

import de.danielbechler.diff.Configuration;
import de.danielbechler.diff.mock.*;

import org.testng.annotations.*;

import java.lang.reflect.*;
import java.util.*;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.fest.assertions.api.Assertions.assertThat;

/** @author Daniel Bechler */
public class PropertyAccessorForCollectionsShould
{
	private PropertyAccessor accessor;

	private ObjectWithCollection target;
	private List<String> targetCollection;

	@BeforeMethod
	public void setUp() throws NoSuchMethodException
	{
		targetCollection = new ArrayList<String>();
		target = new ObjectWithCollection();
		target.setCollection(targetCollection);
		accessor = createCollectionPropertyAccessor(false);
	}

	private static PropertyAccessor createCollectionPropertyAccessor(final boolean readOnly) throws NoSuchMethodException
	{
		final Class<ObjectWithCollection> aClass = ObjectWithCollection.class;
		final Method readMethod = aClass.getMethod("getCollection");
		final Method writeMethod;
		if (readOnly)
		{
			writeMethod = null;
		}
		else
		{
			writeMethod = aClass.getMethod("setCollection", Collection.class);
		}
		return new PropertyAccessor("collection", readMethod, writeMethod, new Configuration());
	}

	@Test
	public void replace_content_of_mutable_target_collection_if_no_setter_is_available() throws Exception
	{
		final List<String> beforeTargetCollection = targetCollection;
		targetCollection.add("bar");
		accessor = createCollectionPropertyAccessor(true);

		accessor.set(target, singletonList("foo"));

		assertThat(targetCollection).containsOnly("foo");
		assertThat(targetCollection).isSameAs(beforeTargetCollection);
	}

	@Test
	public void assign_nothing_if_target_collection_is_null_and_no_write_method_is_available() throws Exception
	{
		targetCollection = null;
		target.setCollection(targetCollection);
		accessor = createCollectionPropertyAccessor(true);

		accessor.set(target, singletonList("foo"));

		assertThat(target.getCollection()).isNull();
	}

	@Test
	public void assign_nothing_if_target_collection_is_immutable_and_no_write_method_is_available() throws Exception
	{
		targetCollection = emptyList();
		target.setCollection(targetCollection);
		accessor = createCollectionPropertyAccessor(true);

		accessor.set(target, singletonList("foo"));

		assertThat(target.getCollection()).isEmpty();
	}
}
