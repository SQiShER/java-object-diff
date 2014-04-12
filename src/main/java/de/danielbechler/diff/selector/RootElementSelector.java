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

package de.danielbechler.diff.selector;

/**
 * @author Daniel Bechler
 */
public final class RootElementSelector extends ElementSelector
{
	private static final RootElementSelector instance = new RootElementSelector();

	RootElementSelector()
	{
	}

	public static RootElementSelector getInstance()
	{
		return instance;
	}

	@Override
	public String toHumanReadableString()
	{
		return "";
	}

	@Override
	public boolean equals(final Object element)
	{
		if (this == element)
		{
			return true;
		}
		if (element != null && getClass().equals(element.getClass()))
		{
			return true;
		}
		return false;
	}

	@Override
	public int hashCode()
	{
		return 0;
	}
}
