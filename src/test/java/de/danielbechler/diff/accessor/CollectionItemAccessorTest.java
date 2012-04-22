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
		final NestableCollectionSafeObject referenceItem = new NestableCollectionSafeObject("a");
		final NestableCollectionSafeObject item1 = new NestableCollectionSafeObject("a", "foo");
		final NestableCollectionSafeObject item2 = new NestableCollectionSafeObject("a", "bar");
		final NestableCollectionSafeObject item3 = new NestableCollectionSafeObject("b", "meh");
		final Iterable<NestableCollectionSafeObject> items = new LinkedHashSet<NestableCollectionSafeObject>(Arrays.asList(item1, item3));

		final Iterator<NestableCollectionSafeObject> iterator1 = items.iterator();
		assertNextItem(iterator1, "a", "foo");
		assertNextItem(iterator1, "b", "meh");

		final Accessor<NestableCollectionSafeObject> accessor = new CollectionItemAccessor<NestableCollectionSafeObject>(referenceItem);
		accessor.set(items, item2);

		final Iterator<NestableCollectionSafeObject> iterator2 = items.iterator();
		assertNextItem(iterator2, "b", "meh");
		assertNextItem(iterator2, "a", "bar"); // appended in the end - maybe there is a way to retain the original position?
	}

	private static void assertNextItem(final Iterator<NestableCollectionSafeObject> iterator, final String key, final String value)
	{
		final NestableCollectionSafeObject next = iterator.next();
		Assert.assertThat(next.getKey(), IsEqual.equalTo(key));
		Assert.assertThat(next.getValue(), IsEqual.equalTo(value));
	}
}
