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

package de.danielbechler.diff;

import org.fest.assertions.api.*;
import org.fest.assertions.core.*;
import org.testng.annotations.*;

import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Bechler
 */
public class DifferProviderShould
{
	private DifferProvider differProvider;

	@BeforeMethod
	public void initDifferProvider()
	{
		differProvider = new DifferProvider();
	}

	@Test
	public void return_differ_that_accepts_given_type()
	{
		final Differ differ = given_differ_accepting_type(String.class);

		final Differ retrievedDiffer = differProvider.retrieveDifferForType(String.class);

		Assertions.assertThat(retrievedDiffer).is(sameAs(differ));
	}

	@Test
	public void return_the_last_pushed_differ_that_accepts_the_given_type()
	{
		final Differ differ = given_differ_accepting_type(String.class);
		final Differ differ2 = given_differ_accepting_type(String.class);

		final Differ retrievedDiffer = differProvider.retrieveDifferForType(String.class);

		Assertions.assertThat(retrievedDiffer).is(sameAs(differ2));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void throw_IllegalArgumentException_if_no_type_is_given()
	{
		differProvider.retrieveDifferForType(null);
	}

	@Test(expectedExceptions = IllegalStateException.class, expectedExceptionsMessageRegExp = "Couldn't find a differ for type: java.util.Date")
	public void throw_IllegalStateException_if_differ_accepts_the_given_type()
	{
		given_differ_accepting_type(String.class);

		differProvider.retrieveDifferForType(Date.class);
	}

	private Differ given_differ_accepting_type(final Class<String> type)
	{
		final Differ differ = mock(Differ.class);
		when(differ.accepts(type)).thenReturn(true);
		differProvider.push(differ);
		return differ;
	}

	private static Condition<Differ> sameAs(final Differ retrievedDiffer)
	{
		return new Condition<Differ>("same as " + retrievedDiffer)
		{
			@Override
			public boolean matches(final Differ differ)
			{
				return differ == retrievedDiffer;
			}
		};
	}
}
