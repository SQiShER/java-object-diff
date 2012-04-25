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

import de.danielbechler.diff.annotation.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public class Configuration implements NodeInspector
{
	private final Collection<String> includedCategories = new TreeSet<String>();
	private final Collection<String> excludedCategories = new TreeSet<String>();
	private final Collection<PropertyPath> includedProperties = new HashSet<PropertyPath>(10);
	private final Collection<PropertyPath> excludedProperties = new HashSet<PropertyPath>(10);
	private final Collection<PropertyPath> equalsOnlyProperties = new LinkedHashSet<PropertyPath>(10);
	private final Collection<Class<?>> equalsOnlyTypes = new LinkedHashSet<Class<?>>(10);
	private boolean returnUnchangedNodes = false;
	private boolean returnIgnoredNodes = false;

	public Configuration withCategory(final String category)
	{
		this.includedCategories.addAll(Arrays.asList(category));
		return this;
	}

	public Configuration withoutCategory(final String... category)
	{
		this.excludedCategories.addAll(Arrays.asList(category));
		return this;
	}

	public Configuration withPropertyPath(final PropertyPath propertyPath)
	{
		this.includedProperties.add(propertyPath);
		return this;
	}

	public Configuration withoutProperty(final PropertyPath propertyPath)
	{
		this.excludedProperties.add(propertyPath);
		return this;
	}

	public Configuration withEqualsOnlyType(final Class<?> type)
	{
		this.equalsOnlyTypes.add(type);
		return this;
	}

	public Configuration withEqualsOnlyProperty(final PropertyPath propertyPath)
	{
		this.equalsOnlyProperties.add(propertyPath);
		return this;
	}

	public Configuration withIgnoredNodes()
	{
		this.returnIgnoredNodes = true;
		return this;
	}

	public Configuration withoutIgnoredNodes()
	{
		this.returnIgnoredNodes = false;
		return this;
	}

	public Configuration withUntouchedNodes()
	{
		this.returnUnchangedNodes = true;
		return this;
	}

	public Configuration withoutUntouchedNodes()
	{
		this.returnUnchangedNodes = false;
		return this;
	}

	@Override
	public boolean isIgnored(final Node node)
	{
		return node.isIgnored() || !isIncluded(node) || isExcluded(node);
	}

	@Override
	public boolean isIncluded(final Node node)
	{
		if (node.isRootNode())
		{
			return true;
		}
		if (includedCategories.isEmpty() && includedProperties.isEmpty())
		{
			return true;
		}
		else if (de.danielbechler.util.Collections.containsAny(node.getCategories(), includedCategories))
		{
			return true;
		}
		else if (includedProperties.contains(node.getPropertyPath()))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isExcluded(final Node node)
	{
		if (excludedProperties.contains(node.getPropertyPath()))
		{
			return true;
		}
		if (de.danielbechler.util.Collections.containsAny(node.getCategories(), excludedCategories))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isEqualsOnly(final Node node)
	{
		final Class<?> propertyType = node.getPropertyType();
		if (propertyType != null)
		{
			if (propertyType.getAnnotation(ObjectDiffEqualsOnlyType.class) != null)
			{
				return true;
			}
			if (equalsOnlyTypes.contains(propertyType))
			{
				return true;
			}
			if (Classes.isSimpleType(propertyType))
			{
				return true;
			}
		}
		if (node.isEqualsOnly())
		{
			return true;
		}
		if (equalsOnlyProperties.contains(node.getPropertyPath()))
		{
			return true;
		}
		return false;
	}

	@Override
	public boolean isReturnable(final Node node)
	{
		if (node.getState() == Node.State.UNTOUCHED)
		{
			return returnUnchangedNodes;
		}
		else if (node.getState() == Node.State.IGNORED)
		{
			return returnIgnoredNodes;
		}
		return true;
	}
}
