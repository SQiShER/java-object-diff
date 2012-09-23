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

import de.danielbechler.diff.integration.*;
import de.danielbechler.diff.node.*;
import org.testng.annotations.*;

import java.net.*;
import java.text.*;
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

	@Test(dataProvider = "wrapperTypes")
	public void testIsWrapperType(final Class<?> type)
	{
		assertThat(Classes.isWrapperType(type)).isTrue();
	}

	@Test(dataProvider = "complexTypes")
	public void testIsNotWrapperType(final Class<?> type)
	{
		assertThat(Classes.isWrapperType(type)).isFalse();
	}

	@Test(dataProvider = "simpleTypes")
	public void testIsSimpleType(final Class<?> type)
	{
		assertThat(Classes.isSimpleType(type)).isTrue();
	}

	@Test(dataProvider = "complexTypes")
	public void testIsNotSimpleType(final Class<?> type)
	{
		assertThat(Classes.isSimpleType(type)).isFalse();
	}

	@DataProvider
	public Object[][] wrapperTypes()
	{
		return new Object[][] {
				new Object[] {Integer.class},
				new Object[] {Short.class},
				new Object[] {Character.class},
				new Object[] {Long.class},
				new Object[] {Boolean.class},
				new Object[] {Byte.class},
				new Object[] {Float.class},
				new Object[] {Double.class},
		};
	}

	@DataProvider
	public Object[][] complexTypes()
	{
		return new Object[][] {
				new Object[] {Contact.class},
				new Object[] {PhoneBook.class},
				new Object[] {PhoneNumber.class},
				new Object[] {Scanner.class},
				new Object[] {Collator.class},
		};
	}

	@DataProvider
	public Object[][] simpleTypes()
	{
		return new Object[][] {
				new Object[] {int.class},
				new Object[] {Integer.class},
				new Object[] {short.class},
				new Object[] {Short.class},
				new Object[] {char.class},
				new Object[] {Character.class},
				new Object[] {long.class},
				new Object[] {Long.class},
				new Object[] {boolean.class},
				new Object[] {Boolean.class},
				new Object[] {byte.class},
				new Object[] {Byte.class},
				new Object[] {float.class},
				new Object[] {Float.class},
				new Object[] {double.class},
				new Object[] {Double.class},
				new Object[] {CharSequence.class},
				new Object[] {String.class},
				new Object[] {Date.class},
				new Object[] {URL.class},
				new Object[] {Locale.class},
				new Object[] {URI.class},
				new Object[] {Number.class},
				new Object[] {Node.State.class},
				new Object[] {Class.class},
		};
	}
}
