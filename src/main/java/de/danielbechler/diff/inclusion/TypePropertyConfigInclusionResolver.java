/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.inclusion;

import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;

import java.util.HashMap;
import java.util.Map;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

/**
 * Created by Daniel Bechler.
 */
class TypePropertyConfigInclusionResolver implements InclusionResolver
{
	private final Map<PropertyId, Inclusion> inclusions = new HashMap<PropertyId, Inclusion>();

	public Inclusion getInclusion(final DiffNode node)
	{
		if (isQualified(node))
		{
			final PropertyId propertyKey = new PropertyId(node.getParentNode().getValueType(), node.getPropertyName());
			final Inclusion inclusion = inclusions.get(propertyKey);
			if (inclusion != null && inclusion != DEFAULT)
			{
				return inclusion;
			}
			else
			{
				if (hasIncludedSiblings(node))
				{
					return EXCLUDED;
				}
			}
		}
		return DEFAULT;
	}

	public boolean enablesStrictIncludeMode()
	{
		return false;
	}

	private static boolean isQualified(final DiffNode node)
	{
		if (node.isPropertyAware())
		{
			if (node.getParentNode() == null || node.getParentNode().getValueType() == null)
			{
				return false;
			}
			if (node.getPropertyName() == null)
			{
				return false;
			}
			return true;
		}
		return false;
	}

	private boolean hasIncludedSiblings(final DiffNode node)
	{
		for (final Map.Entry<PropertyId, Inclusion> entry : inclusions.entrySet())
		{
			if (entry.getKey().type == node.getParentNode().getValueType() && entry.getValue() == INCLUDED)
			{
				return true;
			}
		}
		return false;
	}

	public void setInclusion(final Class<?> type, final String property, final Inclusion inclusion)
	{
		inclusions.put(new PropertyId(type, property), inclusion);
	}

	private static class PropertyId
	{
		private final Class<?> type;
		private final String property;

		private PropertyId(final Class<?> type, final String property)
		{
			Assert.notNull(type, "type");
			Assert.notNull(property, "property");
			this.type = type;
			this.property = property;
		}

		@Override
		public int hashCode()
		{
			int result = type.hashCode();
			result = 31 * result + property.hashCode();
			return result;
		}

		@Override
		public boolean equals(final Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || getClass() != o.getClass())
			{
				return false;
			}

			final PropertyId that = (PropertyId) o;

			if (!property.equals(that.property))
			{
				return false;
			}
			if (!type.equals(that.type))
			{
				return false;
			}

			return true;
		}
	}
}
