/*
 * Copyright 2013 Daniel Bechler
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

import java.util.*;

/** @author Daniel Bechler */
public class DifferProvider
{
	private final List<Differ> differs = new LinkedList<Differ>();

	public void push(final Differ differ)
	{
		differs.add(0, differ);
	}

	public Differ retrieveDifferForType(final Class<?> type)
	{
		if (type == null)
		{
			throw new IllegalArgumentException("Missing 'type'");
		}
		for (final Differ differ : differs)
		{
			if (differ.accepts(type))
			{
				return differ;
			}
		}
		throw new IllegalStateException("Couldn't find a differ for type: " + type.getName());
	}
}
