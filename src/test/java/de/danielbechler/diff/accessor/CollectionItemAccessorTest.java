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

import de.danielbechler.diff.mock.*;
import org.hamcrest.core.*;
import org.testng.annotations.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;

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
		assertThat(next.getKey(), IsEqual.equalTo(key));
		assertThat(next.getValue(), IsEqual.equalTo(value));
	}
}
