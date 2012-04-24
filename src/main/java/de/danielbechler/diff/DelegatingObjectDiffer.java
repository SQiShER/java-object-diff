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

import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;
import de.danielbechler.util.Collections;

import java.util.*;

/** @author Daniel Bechler */
final class DelegatingObjectDiffer implements ObjectDiffer
{
	private final Differ beanDiffer;
	private final Differ mapDiffer;
	private final Differ collectionDiffer;

	private Configuration configuration = new Configuration();

	public DelegatingObjectDiffer()
	{
		this.beanDiffer = new BeanDiffer(this);
		this.mapDiffer = new MapDiffer(this);
		this.collectionDiffer = new CollectionDiffer(this);
	}

	/** Constructor used for lazy initialization of the concrete Differs. */
	public DelegatingObjectDiffer(final Differ beanDiffer,
								  final Differ mapDiffer,
								  final Differ collectionDiffer)
	{
		this.beanDiffer = beanDiffer != null ? beanDiffer : new BeanDiffer(this);
		this.mapDiffer = mapDiffer != null ? mapDiffer : new MapDiffer(this);
		this.collectionDiffer = collectionDiffer != null ? collectionDiffer : new CollectionDiffer(this);
	}

	public <T> Node compare(final T working, final T base)
	{
		return compare(Node.ROOT, Instances.of(working, base));
	}

	public Node compare(final Node parentNode, final Instances instances)
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

	public boolean isIgnored(final Node parentNode, final Instances instances)
	{
		if (instances.getSourceAccessor().isIgnored())
		{
			return true;
		}
		if (Collections.containsAny(instances.getSourceAccessor().getCategories(), configuration.getIgnoreCategories()))
		{
			return true;
		}
		if (configuration.getIgnoreProperties().contains(instances.getPropertyPath(parentNode)))
		{
			return true;
		}
		return false;
	}

	public boolean isEqualsOnly(final Node parentNode, final Instances instances)
	{
		if (instances.getSourceAccessor().isEqualsOnly())
		{
			return true;
		}
		if (configuration.isEqualsOnlyPath(instances.getPropertyPath(parentNode)))
		{
			return true;
		}
		if (configuration.isEqualsOnlyType(instances.getType()))
		{
			return true;
		}
		if (Classes.isSimpleType(instances.getType()))
		{
			return true;
		}
		return false;
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
