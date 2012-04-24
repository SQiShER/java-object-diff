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
import de.danielbechler.diff.path.*;

import java.util.*;

/** @author Daniel Bechler */
public class Configuration
{
	private Collection<String> ignoreCategories = new TreeSet<String>();
	private Collection<PropertyPath> ignoreProperties = new LinkedHashSet<PropertyPath>(10);
	private Collection<PropertyPath> equalsOnlyProperties = new LinkedHashSet<PropertyPath>(10);
	private Collection<Class<?>> equalsOnlyTypes = new LinkedHashSet<Class<?>>(10);
	private boolean returnUnchangedNodes = false;
	private boolean returnIgnoredNodes = false;

	public Collection<String> getIgnoreCategories()
	{
		return Collections.unmodifiableCollection(ignoreCategories);
	}

	public void setIgnoreCategories(final Collection<String> ignoreCategories)
	{
		this.ignoreCategories = ignoreCategories;
	}

	public void addIgnoreCategories(final String... category)
	{
		this.ignoreCategories.addAll(Arrays.asList(category));
	}

	public Collection<PropertyPath> getIgnoreProperties()
	{
		return Collections.unmodifiableCollection(ignoreProperties);
	}

	public void setIgnoreProperties(final Collection<PropertyPath> ignoreProperties)
	{
		this.ignoreProperties = ignoreProperties;
	}

	public void addIgnoreProperty(final PropertyPath propertyPath)
	{
		this.ignoreProperties.add(propertyPath);
	}

	public Collection<PropertyPath> getEqualsOnlyProperties()
	{
		return Collections.unmodifiableCollection(equalsOnlyProperties);
	}

	public void setEqualsOnlyProperties(final Collection<PropertyPath> equalsOnlyProperties)
	{
		this.equalsOnlyProperties = equalsOnlyProperties;
	}

	public void addEqualsOnlyProperty(final PropertyPath propertyPath)
	{
		this.equalsOnlyProperties.add(propertyPath);
	}

	public Collection<Class<?>> getEqualsOnlyTypes()
	{
		return Collections.unmodifiableCollection(equalsOnlyTypes);
	}

	public void setEqualsOnlyTypes(final Collection<Class<?>> equalsOnlyTypes)
	{
		this.equalsOnlyTypes = equalsOnlyTypes;
	}

	public void addEqualsOnlyType(final Class<?> type)
	{
		this.equalsOnlyTypes.add(type);
	}

	public boolean isEqualsOnlyPath(final PropertyPath selectorPath)
	{
		return equalsOnlyProperties.contains(selectorPath);
	}

	public boolean isEqualsOnlyType(final Class<?> type)
	{
		if (type.getAnnotation(ObjectDiffEqualsOnlyType.class) != null)
		{
			return true;
		}
		else if (equalsOnlyTypes.contains(type))
		{
			return true;
		}
		return false;
	}

	public boolean isReturnIgnoredNodes()
	{
		return returnIgnoredNodes;
	}

	public void setReturnIgnoredNodes(final boolean returnIgnoredNodes)
	{
		this.returnIgnoredNodes = returnIgnoredNodes;
	}

	public boolean isReturnUnchangedNodes()
	{
		return returnUnchangedNodes;
	}

	public void setReturnUnchangedNodes(final boolean returnUnchangedNodes)
	{
		this.returnUnchangedNodes = returnUnchangedNodes;
	}
}
