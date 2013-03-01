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

package de.danielbechler.diff.extension;

import org.mockito.invocation.*;
import org.mockito.stubbing.*;

/** @author Daniel Bechler */
public class MockitoExtensions
{
	private MockitoExtensions()
	{
	}

	public static <T> Answer<Class<T>> returnClass(final Class<T> clazz)
	{
		return new Answer<Class<T>>()
		{
			public Class<T> answer(final InvocationOnMock invocation) throws Throwable
			{
				return clazz;
			}
		};
	}

}
