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

package de.danielbechler.diff.path;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
public final class NamedPropertyElement extends Element
{
	private final String propertyName;

	public NamedPropertyElement(final String propertyName)
	{
		Assert.hasText(propertyName, "propertyName");
		this.propertyName = propertyName;
	}

	public String getPropertyName()
	{
		return propertyName;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final NamedPropertyElement that = (NamedPropertyElement) o;

		if (!propertyName.equals(that.propertyName))
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		return propertyName.hashCode();
	}

	@Override
	public String toString()
	{
		return propertyName;
	}
}
