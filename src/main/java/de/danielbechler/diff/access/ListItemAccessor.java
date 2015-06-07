/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.access;

import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.diff.selector.ListItemSelector;

public class ListItemAccessor implements Accessor
{
	private final Object referenceItem;
	private final int ordinal;
	private final ListItemSelector selector;

	public ListItemAccessor(Object referenceItem, int ordinal)
	{

		this.referenceItem = referenceItem;
		this.ordinal = ordinal;
		this.selector = new ListItemSelector(referenceItem, ordinal);
	}

	public ElementSelector getElementSelector()
	{
		return selector;
	}

	public Object get(final Object target)
	{
		return null;
	}

	public void set(Object target, Object value)
	{

	}

	public void unset(Object target)
	{

	}
}
