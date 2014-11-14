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

package de.danielbechler.diff.filtering;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;

import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.danielbechler.util.Collections.filteredCopyOf;
import static java.util.Arrays.asList;

/**
 * @author Daniel Bechler
 */
public class ReturnableNodeService implements FilteringConfigurer, IsReturnableResolver
{
	private final Map<DiffNode.State, Boolean> stateFilterSettings;
	private final ObjectDifferBuilder objectDifferBuilder;

	public ReturnableNodeService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
		this.stateFilterSettings = new EnumMap<DiffNode.State, Boolean>(DiffNode.State.class);
		this.stateFilterSettings.put(DiffNode.State.IGNORED, false);
		this.stateFilterSettings.put(DiffNode.State.INACCESSIBLE, false);
		this.stateFilterSettings.put(DiffNode.State.UNTOUCHED, false);
		this.stateFilterSettings.put(DiffNode.State.CIRCULAR, true);
		this.stateFilterSettings.put(DiffNode.State.ADDED, true);
		this.stateFilterSettings.put(DiffNode.State.REMOVED, true);
		this.stateFilterSettings.put(DiffNode.State.CHANGED, true);
		assertDefaultValuesForAllAvailableStates();
	}

	private void assertDefaultValuesForAllAvailableStates()
	{
		final List<DiffNode.State> availableStates = asList(DiffNode.State.values());
		final Set<DiffNode.State> statesWithDefaultValue = stateFilterSettings.keySet();
		final Collection<? extends DiffNode.State> statesWithoutDefaultValue = filteredCopyOf(availableStates, statesWithDefaultValue);
		if (!statesWithoutDefaultValue.isEmpty())
		{
			throw new IllegalStateException("Missing default value for states: " + statesWithoutDefaultValue);
		}
	}

	public boolean isReturnable(final DiffNode node)
	{
		if (node.isRootNode())
		{
			return true;
		}

		if (node.isUntouched() && node.hasChildren())
		{
			return true;
		}

		return stateFilterSettings.get(node.getState());
	}

	public ReturnableNodeService returnNodesWithState(final DiffNode.State state, final boolean enabled)
	{
		this.stateFilterSettings.put(state, enabled);
		return this;
	}

	public ReturnableNodeService returnNodesWithState(final DiffNode.State state)
	{
		return returnNodesWithState(state, true);
	}

	public ReturnableNodeService omitNodesWithState(final DiffNode.State state)
	{
		return returnNodesWithState(state, false);
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}
}
