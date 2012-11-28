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

import de.danielbechler.diff.path.*;
import org.testng.annotations.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.core.IsSame.*;

/** @author Daniel Bechler */
public class RootAccessorTest
{
	private final Accessor accessor = RootAccessor.getInstance();

	@Test
	public void testGet() throws Exception
	{
		final Object root = new Object();
		assertThat(accessor.get(root), sameInstance(root));
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testSet() throws Exception
	{
		final Object original = new Object();
		final Object replacement = new Object();
		accessor.set(original, replacement);
	}

	@Test(expectedExceptions = UnsupportedOperationException.class)
	public void testUnset() throws Exception
	{
		final Object original = new Object();
		accessor.unset(original);
	}

	@Test
	public void testToPathElement() throws Exception
	{
		assertThat(accessor.getPathElement(), is(instanceOf(RootElement.class)));
	}
}
