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
	private boolean returnCircularNodes = true;

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

	public Configuration withCircularNodes()
	{
		this.returnCircularNodes = true;
		return this;
	}

	public Configuration withoutCircularNodes()
	{
		this.returnCircularNodes = false;
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
		final Class<?> propertyType = node.getType();
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
		if (node.isUntouched())
		{
			return returnUnchangedNodes;
		}
		else if (node.isIgnored())
		{
			return returnIgnoredNodes;
		}
		else if (node.isCircular())
		{
			return returnCircularNodes;
		}
		return true;
	}
}
