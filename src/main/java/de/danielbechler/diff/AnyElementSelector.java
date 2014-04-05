/*
 * Copyright 2014 Daniel Bechler
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

/**
 * Created by Daniel Bechler.
 *
 * @deprecated Not very intuitive to work with (at least not internally)
 */
@Deprecated
public final class AnyElementSelector extends ElementSelector
{
	@Override
	public String toHumanReadableString()
	{
		return "*";
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (obj != null && obj instanceof AnyElementSelector)
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
