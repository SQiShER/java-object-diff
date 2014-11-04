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

import java.util.HashMap;
import java.util.Map;

import static de.danielbechler.diff.inclusion.Inclusion.DEFAULT;
import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED;
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED;

class PropertyNameInclusionResolver implements InclusionResolver
{
	private final Map<String, Inclusion> propertyNameInclusions = new HashMap<String, Inclusion>();
	private boolean containsIncluded;
	private boolean containsExcluded;

	public Inclusion getInclusion(final DiffNode node)
	{
		if (node != null && !isInactive())
		{
			final Inclusion inclusion = propertyNameInclusions.get(node.getPropertyName());
			if (inclusion == INCLUDED || inclusion == EXCLUDED)
			{
				return inclusion;
			}
			final Inclusion parentNodeInclusion = getInclusion(node.getParentNode());
			if (parentNodeInclusion == INCLUDED)
			{
				return INCLUDED;
			}
		}
		return DEFAULT;
	}

	public boolean enablesStrictIncludeMode()
	{
		return containsIncluded;
	}

	private boolean isInactive()
	{
		return !containsIncluded && !containsExcluded;
	}

	public void setInclusion(final String propertyName, final Inclusion inclusion)
	{
		propertyNameInclusions.put(propertyName, inclusion);
		containsIncluded = propertyNameInclusions.containsValue(INCLUDED);
		containsExcluded = propertyNameInclusions.containsValue(EXCLUDED);
	}
}
