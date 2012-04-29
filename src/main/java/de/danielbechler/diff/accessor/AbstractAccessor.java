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

import java.util.*;

/** @author Daniel Bechler */
public abstract class AbstractAccessor implements Accessor
{
	private Set<String> categories = new TreeSet<String>();
	private boolean equalsOnly;
	private boolean ignored;

	public final Set<String> getCategories()
	{
		return categories;
	}

	public final void setCategories(final Set<String> categories)
	{
		this.categories = categories;
	}

	public boolean isEqualsOnly()
	{
		return equalsOnly;
	}

	public void setEqualsOnly(final boolean equalsOnly)
	{
		this.equalsOnly = equalsOnly;
	}

	public boolean isIgnored()
	{
		return ignored;
	}

	public void setIgnored(final boolean ignored)
	{
		this.ignored = ignored;
	}
}
