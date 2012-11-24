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

import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/**
 * This is the entry point for all comparisons. It determines the type of the given objects and passes them to
 * the appropriate {@link Differ}.
 *
 * @author Daniel Bechler
 */
class DelegatingObjectDiffer implements ObjectDiffer
{
	private final Configuration configuration;
	private DifferFactory differFactory;
	private DifferDelegator differDelegator;

	DelegatingObjectDiffer(final Configuration configuration)
	{
		this.configuration = configuration;
		this.differFactory = new DifferFactory(configuration, this);
		this.differDelegator = new DifferDelegator(differFactory);
	}

	private DifferFactory getDifferFactory()
	{
		if (differFactory == null)
		{
			differFactory = new DifferFactory(configuration, this);
		}
		return differFactory;
	}

	void setDifferFactory(final DifferFactory differFactory)
	{
		this.differFactory = differFactory;
	}

	public <T> Node compare(final T working, final T base)
	{
		return differDelegator.delegate(Node.ROOT, Instances.of(working, base));
	}

	/**
	 * Delegates the call to an appropriate {@link de.danielbechler.diff.Differ}.
	 *
	 * @return A node representing the difference between the given {@link de.danielbechler.diff.Instances}.
	 */
	public Node delegate(final Node parentNode, final Instances instances)
	{
		Assert.notNull(instances, "instances");
		if (instances.getType() == null)
		{
			if (instances.areNull())
			{
				return new DefaultNode(parentNode, instances.getSourceAccessor(), instances.getType());
			}
			else
			{
				throw new IllegalArgumentException("Instances didn't return a type. That shouldn't happen and must be a bug!");
			}
		}
		return findDifferFor(instances).compare(parentNode, instances);
	}

	private Differ<?> findDifferFor(final Instances instances)
	{
		return getDifferFactory().createDiffer(instances.getType());
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}
}
