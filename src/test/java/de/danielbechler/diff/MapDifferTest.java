package de.danielbechler.diff;

import de.danielbechler.diff.node.*;
import org.junit.*;

import java.util.*;

import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;
import static org.junit.Assert.*;

/** @author Daniel Bechler */
public class MapDifferTest
{
	private MapDiffer mapDiffer;

	@Before
	public void setUp()
	{
		mapDiffer = new MapDiffer();
	}

	@Test
	public void testWithAdditionOfSimpleTypeToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode<String, String> node = mapDiffer.compare(working, base);
		assertThat(node.isMapDifference(), is(true));
		assertThat(node.hasChildren(), is(true));
		assertThat(node.getType(), is(DifferenceType.CHANGED));

		final Collection<DiffNode<?>> children = node.getChildren();
		assertThat(children.size(), is(1));

		final DiffNode<?> child = children.iterator().next();
		assertThat((String) child.getAccessor().get(working), equalTo("bar"));
		assertThat(child.getAccessor().get(base), nullValue());
		assertThat(child.getType(), is(DifferenceType.ADDED));
	}

	@Test
	public void testWithNewMapInWorkingAndNoneInBase()
	{
		final Map<String, String> base = null;
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode<String, String> node = mapDiffer.compare(working, base);
		assertThat(node.getType(), is(DifferenceType.ADDED));

		final Collection<DiffNode<?>> children = node.getChildren();
		assertThat(children.size(), is(1));

		final DiffNode<?> child = children.iterator().next();
		assertThat(child.getType(), is(DifferenceType.ADDED));
	}

	@Test
	public void testWithSameEntryInBaseAndWorking()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode<String, String> node = mapDiffer.compare(working, base);
		assertThat(node.getType(), is(DifferenceType.UNTOUCHED));
		assertThat(node.hasChildren(), is(false));
	}

	@Test
	public void testWithSingleEntryAddedToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = null;

		final MapNode<String, String> node = mapDiffer.compare(working, base);
		assertThat(node.getType(), is(DifferenceType.REMOVED));

		final Collection<DiffNode<?>> children = node.getChildren();
		assertThat(children.size(), is(1));

		final DiffNode<?> child = children.iterator().next();
		assertThat(child.getType(), is(DifferenceType.REMOVED));
	}

	@Test
	public void testWithoutMapInBaseAndWorking()
	{
		final MapNode<Object, Object> node = mapDiffer.compare((Map) null, null);
		assertThat(node.getType(), is(DifferenceType.UNTOUCHED));
		assertThat(node.hasChildren(), is(false));
	}
}
