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

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
class DifferDelegator
{
	private final DifferFactory differFactory;

	public DifferDelegator(final DifferFactory differFactory)
	{
		this.differFactory = differFactory;
	}

	/**
	 * Delegates the call to an appropriate {@link de.danielbechler.diff.Differ}.
	 *
	 * @return A node representing the difference between the given {@link de.danielbechler.diff.Instances}.
	 */
	public Node delegate(final Node parentNode, final Instances instances)
	{
		Assert.notNull(instances, "instances");
		final Class<?> type = instances.getType();
		if (type == null)
		{
			if (instances.areNull())
			{
				final Accessor accessor = instances.getSourceAccessor();
				return new DefaultNode(parentNode, accessor, type);
			}
			else
			{
				throw new IllegalArgumentException("Instances didn't return a type. That shouldn't happen and must be a bug!");
			}
		}
		return differFactory.createDiffer(type).compare(parentNode, instances);
	}
}
