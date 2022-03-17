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

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.diff.introspection.ObjectDiffProperty;
import de.danielbechler.diff.node.DiffNode;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;
import static java.util.Collections.emptyList;

/**
 * Created by Daniel Bechler.
 */
public class TypePropertyAnnotationInclusionResolver implements InclusionResolver
{
	public boolean enablesStrictIncludeMode()
	{
		return false;
	}

	public Inclusion getInclusion(final DiffNode node)
	{
		final ObjectDiffProperty propertyAnnotation = node.getPropertyAnnotation(ObjectDiffProperty.class);
		if (propertyAnnotation != null)
		{
			if (propertyAnnotation.inclusion() == INCLUDED || propertyAnnotation.inclusion() == EXCLUDED)
			{
				return propertyAnnotation.inclusion();
			}
			else if (propertyAnnotation.excluded())
			{
				return EXCLUDED;
			}
		}
		else if (hasIncludedSibling(node))
		{
			return EXCLUDED;
		}
		return DEFAULT;
	}

	private static boolean hasIncludedSibling(final DiffNode node)
	{
		for (final PropertyAwareAccessor accessor : getSiblingAccessors(node))
		{
			final ObjectDiffProperty annotation = accessor.getReadMethodAnnotation(ObjectDiffProperty.class);
			if (annotation != null && annotation.inclusion() == INCLUDED)
			{
				return true;
			}
		}
		return false;
	}

	private static Iterable<PropertyAwareAccessor> getSiblingAccessors(final DiffNode node)
	{
		final DiffNode parentNode = node.getParentNode();
		if (parentNode != null)
		{
			final TypeInfo typeInfo = parentNode.getValueTypeInfo();
			if (typeInfo != null)
			{
				return typeInfo.getAccessors();
			}
		}
		return emptyList();
	}
}
