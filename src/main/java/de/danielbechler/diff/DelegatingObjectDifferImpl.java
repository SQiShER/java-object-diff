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

import java.util.*;

/**
 * This is the entry point for all comparisons. It determines the type of the given objects and passes them to
 * the appropriate {@link Differ}.
 *
 * @author Daniel Bechler
 */
final class DelegatingObjectDifferImpl implements DelegatingObjectDiffer
{
	private final Differ beanDiffer;
	private final Differ mapDiffer;
	private final Differ collectionDiffer;

	private Configuration configuration = new Configuration();

	public DelegatingObjectDifferImpl()
	{
		this.beanDiffer = new BeanDiffer(this);
		this.mapDiffer = new MapDiffer(this);
		this.collectionDiffer = new CollectionDiffer(this);
	}

	/** Constructor used for lazy initialization of the concrete Differs. */
	public DelegatingObjectDifferImpl(final Differ beanDiffer,
									  final Differ mapDiffer,
									  final Differ collectionDiffer)
	{
		this.beanDiffer = beanDiffer != null ? beanDiffer : new BeanDiffer(this);
		this.mapDiffer = mapDiffer != null ? mapDiffer : new MapDiffer(this);
		this.collectionDiffer = collectionDiffer != null ? collectionDiffer : new CollectionDiffer(this);
	}

	public <T> Node compare(final T working, final T base)
	{
		return delegate(Node.ROOT, Instances.of(working, base));
	}

	/**
	 * Delegates the comparison to the appropriate {@link Differ}.
	 *
	 * @return A node representing the difference between the given {@link Instances}.
	 */
	public Node delegate(final Node parentNode, final Instances instances)
	{
		if (Collection.class.isAssignableFrom(instances.getType()))
		{
			return collectionDiffer.compare(parentNode, instances);
		}
		else if (Map.class.isAssignableFrom(instances.getType()))
		{
			return mapDiffer.compare(parentNode, instances);
		}
		else
		{
			return beanDiffer.compare(parentNode, instances);
		}
	}

	public boolean isIgnored(final Node node)
	{
		return configuration.isIgnored(node);
	}

	@Override
	public boolean isIncluded(final Node node)
	{
		return configuration.isIncluded(node);
	}

	@Override
	public boolean isExcluded(final Node node)
	{
		return configuration.isExcluded(node);
	}

	public boolean isEqualsOnly(final Node node)
	{
		return configuration.isEqualsOnly(node);
	}

	@Override
	public boolean isReturnable(final Node node)
	{
		return configuration.isReturnable(node);
	}

	public Configuration getConfiguration()
	{
		return configuration;
	}

	public void setConfiguration(final Configuration configuration)
	{
		Assert.notNull(configuration, "configuration");
		this.configuration = configuration;
	}

	// Test Helpers

	Differ getBeanDiffer()
	{
		return beanDiffer;
	}

	Differ getMapDiffer()
	{
		return mapDiffer;
	}

	Differ getCollectionDiffer()
	{
		return collectionDiffer;
	}
}
