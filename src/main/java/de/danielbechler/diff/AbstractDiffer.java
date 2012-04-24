/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.danielbechler.diff;

import de.danielbechler.util.*;

/** @author Daniel Bechler */
abstract class AbstractDiffer implements Differ, Configurable
{
	private DelegatingObjectDiffer delegate;

	protected AbstractDiffer()
	{
	}

	protected AbstractDiffer(final DelegatingObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	public final DelegatingObjectDiffer getDelegate()
	{
		return delegate;
	}

	public final void setDelegate(final DelegatingObjectDiffer delegate)
	{
		Assert.notNull(delegate, "delegate");
		this.delegate = delegate;
	}

	public final Configuration getConfiguration()
	{
		return delegate.getConfiguration();
	}
}
