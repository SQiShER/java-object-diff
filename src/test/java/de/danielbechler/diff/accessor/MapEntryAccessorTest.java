package de.danielbechler.diff.accessor;

import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.util.*;

/** @author Daniel Bechler */
public class MapEntryAccessorTest
{
	private MapEntryAccessor<String, String> accessor;

	@Before
	public void setUp()
	{
		accessor = new MapEntryAccessor<String, String>(Arrays.asList("a", "b", "c"), 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructionWithInvalidIndex()
	{
		new MapEntryAccessor<String, String>(Arrays.asList("foo"), 5);
	}

	@Test
	public void testGetPropertyName() throws Exception
	{
		Assert.assertThat(accessor.getPropertyName(), IsEqual.equalTo("[1]"));
	}

	@Test
	public void testToPathElement() throws Exception
	{
		Assert.assertThat(accessor.toPathElement(), Is.is(MapElement.class));
	}

	@Test
	public void testGetPath() throws Exception
	{
		Assert.assertThat(accessor.getPath(), IsEqual.equalTo(new PropertyPath(new MapElement<String>("b"))));
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
		Assert.assertThat(accessor.get(map), IsEqual.equalTo("foo"));
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
		accessor.unset(map, "b");
		Assert.assertTrue(map.isEmpty());
	}
}
