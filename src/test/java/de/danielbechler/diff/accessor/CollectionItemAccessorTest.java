package de.danielbechler.diff.accessor;

import de.danielbechler.diff.mock.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.util.*;

/** @author Daniel Bechler */
public class CollectionItemAccessorTest
{
	@Test
	public void testSet()
	{
		final ObjectWithHashCodeAndEquals referenceItem = new ObjectWithHashCodeAndEquals("a");
		final ObjectWithHashCodeAndEquals item1 = new ObjectWithHashCodeAndEquals("a", "foo");
		final ObjectWithHashCodeAndEquals item2 = new ObjectWithHashCodeAndEquals("a", "bar");
		final ObjectWithHashCodeAndEquals item3 = new ObjectWithHashCodeAndEquals("b", "meh");
		final Iterable<ObjectWithHashCodeAndEquals> items =
				new LinkedHashSet<ObjectWithHashCodeAndEquals>(Arrays.asList(item1, item3));

		final Iterator<ObjectWithHashCodeAndEquals> iterator1 = items.iterator();
		assertNextItem(iterator1, "a", "foo");
		assertNextItem(iterator1, "b", "meh");

		final Accessor accessor = new CollectionItemAccessor(referenceItem);
		accessor.set(items, item2);

		final Iterator<ObjectWithHashCodeAndEquals> iterator2 = items.iterator();
		assertNextItem(iterator2, "b", "meh");
		assertNextItem(iterator2, "a", "bar"); // appended in the end - maybe there is a way to retain the original position?
	}

	private static void assertNextItem(final Iterator<ObjectWithHashCodeAndEquals> iterator,
									   final String key,
									   final String value)
	{
		final ObjectWithHashCodeAndEquals next = iterator.next();
		Assert.assertThat(next.getKey(), IsEqual.equalTo(key));
		Assert.assertThat(next.getValue(), IsEqual.equalTo(value));
	}
}
