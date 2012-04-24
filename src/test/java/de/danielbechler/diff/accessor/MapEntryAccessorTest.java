package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.util.*;

/** @author Daniel Bechler */
public class MapEntryAccessorTest
{
	private MapEntryAccessor accessor;

	@Before
	public void setUp()
	{
		accessor = new MapEntryAccessor(Arrays.asList("a", "b", "c"), 1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testConstructionWithInvalidIndex()
	{
		new MapEntryAccessor(Arrays.asList("foo"), 5);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.getPathElement(), Is.is(MapElement.class));
	}

	@Test
	public void testSet() throws Exception
	{
		final TreeMap<String, String> map = new TreeMap<String, String>();

		accessor.set(map, "foo");
		Assert.assertThat(map.get("b"), IsEqual.equalTo("foo"));

		accessor.set(map, "bar");
		Assert.assertThat(map.get("b"), IsEqual.equalTo("bar"));
	}

	@Test
	public void testGetFromMap() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		Assert.assertThat((String) accessor.get(map), IsEqual.equalTo("foo"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetFromInvalidObjectType()
	{
		accessor.get(new Object());
	}

	@Test
	public void testGetFromNull()
	{
		Assert.assertThat(accessor.get(null), IsNull.nullValue());
	}

	@Test
	public void testUnset() throws Exception
	{
		final Map<String, String> map = new TreeMap<String, String>();
		map.put("b", "foo");
		accessor.unset(map);
		Assert.assertTrue(map.isEmpty());
	}
}
