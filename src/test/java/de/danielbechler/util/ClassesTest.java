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

package de.danielbechler.util;

import org.junit.*;

import java.util.*;

import static org.fest.assertions.api.Assertions.*;

/** @author Daniel Bechler */
public class ClassesTest
{
	@Test
	public void testAllAssignableFrom_returns_true_when_all_items_share_the_expected_type() throws Exception
	{
		final Collection<Class<?>> items = new ArrayList<Class<?>>(2);
		items.add(ArrayList.class);
		items.add(LinkedList.class);
		final boolean result = Classes.allAssignableFrom(List.class, items);
		assertThat(result).isTrue();
	}

	@Test
	public void testAllAssignableFrom_returns_false_if_not_all_items_share_the_expected_type() throws Exception
	{
		final Collection<Class<?>> items = new ArrayList<Class<?>>(2);
		items.add(Object.class);
		items.add(LinkedList.class);
		final boolean result = Classes.allAssignableFrom(List.class, items);
		assertThat(result).isFalse();
	}
}
