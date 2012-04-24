/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

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
