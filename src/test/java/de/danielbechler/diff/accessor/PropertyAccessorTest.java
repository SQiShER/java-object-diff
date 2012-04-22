package de.danielbechler.diff.accessor;

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
public class PropertyAccessorTest
{
	private PropertyAccessor<String> accessor;

	@Before
	public void setUp() throws Exception
	{
		final Method readMethod = NestableCollectionSafeObject.class.getMethod("getValue");
		final Method writeMethod = NestableCollectionSafeObject.class.getMethod("setValue", String.class);
		accessor = new PropertyAccessor<String>("value", readMethod, writeMethod);
	}

	@Test
	public void testSet() throws Exception
	{
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
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
		accessor = new PropertyAccessor<String>("value", readMethod, writeMethod);
		accessor.set(target, "bar");
	}

	@Test
	public void testSetWithoutSetter() throws Exception
	{
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		accessor = new PropertyAccessor<String>("value", NestableCollectionSafeObject.class.getMethod("getValue"), null);
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
		accessor = new PropertyAccessor<String>("map", readMethod, writeMethod);
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
		accessor = new PropertyAccessor<String>("collection", readMethod, writeMethod);
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
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		item.setValue("bar");
		Assert.assertThat(accessor.get(item), IsEqual.equalTo("bar"));
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
		accessor = new PropertyAccessor<String>("value", readMethod, null);
		accessor.get(new ObjectWithMap());
	}

	@Test
	public void testUnset() throws Exception
	{
		final NestableCollectionSafeObject item = new NestableCollectionSafeObject("foo");
		item.setValue("bar");
		accessor.unset(item, null);
		Assert.assertThat(item.getValue(), IsNull.nullValue());
	}

	@Test
	public void testGetType() throws Exception
	{
		Assert.assertThat(accessor.getType(), IsEqual.equalTo(String.class));
	}

	@Test
	public void testGetPropertyName() throws Exception
	{
		Assert.assertThat(accessor.getPropertyName(), IsEqual.equalTo("value"));
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat((NamedPropertyElement) accessor.toPathElement(), IsEqual.equalTo(new NamedPropertyElement("value")));
	}

	@Test
	public void testGetPath() throws Exception
	{
		Assert.assertThat(accessor.getPath(), IsEqual.equalTo(new PropertyPath(new NamedPropertyElement("value"))));
	}
}
