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

import de.danielbechler.diff.accessor.exception.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
public class PropertyAccessorTest
{
	private PropertyAccessor accessor;

	@Before
	public void setUp() throws Exception
	{
		final Method readMethod = ObjectWithHashCodeAndEquals.class.getMethod("getValue");
		final Method writeMethod = ObjectWithHashCodeAndEquals.class.getMethod("setValue", String.class);
		accessor = new PropertyAccessor("value", readMethod, writeMethod);
	}

	@Test
	public void testSet() throws Exception
	{
		final ObjectWithHashCodeAndEquals item = new ObjectWithHashCodeAndEquals("foo");
		Assert.assertThat(item.getValue(), IsNull.nullValue());
		accessor.set(item, "bar");
		Assert.assertThat(item.getValue(), IsEqual.equalTo("bar"));
	}

	@Test(expected = PropertyWriteException.class)
	public void testSetWithUnsupportedWriteMethod() throws NoSuchMethodException
	{
		final ObjectWithStringAndUnsupportedWriteMethod target = new ObjectWithStringAndUnsupportedWriteMethod("foo");
		final Method readMethod = target.getClass().getMethod("getValue");
		final Method writeMethod = target.getClass().getMethod("setValue", String.class);
		accessor = new PropertyAccessor("value", readMethod, writeMethod);
		accessor.set(target, "bar");
	}

	@Test
	public void testSetWithoutSetter() throws Exception
	{
		final ObjectWithHashCodeAndEquals item = new ObjectWithHashCodeAndEquals("foo");
		accessor = new PropertyAccessor("value", ObjectWithHashCodeAndEquals.class.getMethod("getValue"), null);
		accessor.set(item, "bar");
		Assert.assertThat(item.getValue(), IsNull.nullValue());
	}

	@Test
	public void testSetWithNullTarget() throws Exception
	{
		accessor.set(null, "bar"); // just to make sure no exception is thrown
	}

	@Test
	public void testSetMapWithoutSetter() throws Exception
	{
		final ObjectWithMap objectWithMap = initMapAccessor(true);
		accessor.set(objectWithMap, Collections.singletonMap("foo", "bar"));
		final Map<String, String> resultMap = objectWithMap.getMap();
		Assert.assertThat(resultMap.get("foo"), IsEqual.equalTo("bar"));
	}

	@Test
	public void testSetMapWithoutSetterAndUnmodifiableTargetMap() throws Exception
	{
		final Map<String, String> map = Collections.unmodifiableMap(new TreeMap<String, String>());
		final ObjectWithMap objectWithMap = initMapAccessor(map, true);
		accessor.set(objectWithMap, Collections.singletonMap("foo", "bar"));
		Assert.assertThat(objectWithMap.getMap().get("foo"), IsNull.nullValue());
	}

	@Test
	public void testSetMapWithoutSetterAndNullTargetMap() throws Exception
	{
		final ObjectWithMap objectWithMap = initMapAccessor(null, true);
		accessor.set(objectWithMap, Collections.singletonMap("foo", "bar"));
		Assert.assertThat(objectWithMap.getMap(), IsNull.nullValue());
	}

	private ObjectWithMap initMapAccessor(final boolean omitWriteMethod) throws NoSuchMethodException
	{
		return initMapAccessor(new TreeMap<String, String>(), omitWriteMethod);
	}

	private ObjectWithMap initMapAccessor(final Map<String, String> map,
										  final boolean omitWriteMethod) throws NoSuchMethodException
	{
		final ObjectWithMap objectWithMap = new ObjectWithMap();
		objectWithMap.setMap(map);
		final Method readMethod = ObjectWithMap.class.getMethod("getMap");
		final Method writeMethod;
		if (omitWriteMethod)
		{
			writeMethod = null;
		}
		else
		{
			writeMethod = ObjectWithMap.class.getMethod("setMap", Map.class);
		}
		accessor = new PropertyAccessor("map", readMethod, writeMethod);
		return objectWithMap;
	}

	private ObjectWithCollection initCollectionAccessor(final Collection<String> collection,
														final boolean omitWriteMethod) throws NoSuchMethodException
	{
		final ObjectWithCollection object = new ObjectWithCollection();
		object.setCollection(collection);
		final Method readMethod = object.getClass().getMethod("getCollection");
		final Method writeMethod;
		if (omitWriteMethod)
		{
			writeMethod = null;
		}
		else
		{
			writeMethod = object.getClass().getMethod("setCollection", Collection.class);
		}
		accessor = new PropertyAccessor("collection", readMethod, writeMethod);
		return object;
	}

	@Test
	public void testSetCollectionWithoutSetter() throws NoSuchMethodException
	{
		final ObjectWithCollection target = initCollectionAccessor(new LinkedList<String>(), true);
		accessor.set(target, Collections.singletonList("foo"));
		Assert.assertThat(target.getCollection().iterator().next(), IsEqual.equalTo("foo"));
	}

	@Test
	public void testSetCollectionWithoutSetterAndUnmodifiableCollection() throws NoSuchMethodException
	{
		final List<String> targetList = Collections.unmodifiableList(new LinkedList<String>());
		final ObjectWithCollection target = initCollectionAccessor(targetList, true);
		accessor.set(target, Collections.singletonList("foo"));
		Assert.assertThat(target.getCollection().contains("foo"), Is.is(false));
	}

	@Test
	public void testSetCollectionWithoutSetterAndNullTargetCollection() throws NoSuchMethodException
	{
		final List<String> targetList = null;
		final ObjectWithCollection target = initCollectionAccessor(targetList, true);
		accessor.set(target, Collections.singletonList("foo"));
		Assert.assertThat(target.getCollection(), IsNull.nullValue());
	}

	@Test
	public void testGet() throws Exception
	{
		final ObjectWithHashCodeAndEquals item = new ObjectWithHashCodeAndEquals("foo");
		item.setValue("bar");
		Assert.assertThat((String) accessor.get(item), IsEqual.equalTo("bar"));
	}

	@Test
	public void testGetWithNullTarget()
	{
		Assert.assertThat(accessor.get(null), IsNull.nullValue());
	}

	@Test(expected = PropertyReadException.class)
	public void testGetWithInvocationException() throws NoSuchMethodException
	{
		final Method readMethod = ObjectWithString.class.getMethod("getValue");
		accessor = new PropertyAccessor("value", readMethod, null);
		accessor.get(new ObjectWithMap());
	}

	@Test
	public void testUnset() throws Exception
	{
		final ObjectWithHashCodeAndEquals item = new ObjectWithHashCodeAndEquals("foo");
		item.setValue("bar");
		accessor.unset(item);
		Assert.assertThat(item.getValue(), IsNull.nullValue());
	}

	@Test
	public void testGetType() throws Exception
	{
		//noinspection unchecked
		Assert.assertThat((Class<String>) accessor.getType(), IsEqual.equalTo(String.class));
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat((NamedPropertyElement) accessor.getPathElement(), IsEqual.equalTo(new NamedPropertyElement("value")));
	}
}
