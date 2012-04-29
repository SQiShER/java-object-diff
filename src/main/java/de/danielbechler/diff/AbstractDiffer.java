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
