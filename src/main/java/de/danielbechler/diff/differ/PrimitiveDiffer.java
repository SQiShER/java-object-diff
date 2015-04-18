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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.comparison.PrimitiveDefaultValueModeResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Classes;

import static de.danielbechler.diff.comparison.PrimitiveDefaultValueMode.UNASSIGNED;

/**
 * @author Daniel Bechler
 */
public final class PrimitiveDiffer implements Differ
{
	private final PrimitiveDefaultValueModeResolver primitiveDefaultValueModeResolver;

	public PrimitiveDiffer(final PrimitiveDefaultValueModeResolver primitiveDefaultValueModeResolver)
	{
		Assert.notNull(primitiveDefaultValueModeResolver, "primitiveDefaultValueModeResolver");
		this.primitiveDefaultValueModeResolver = primitiveDefaultValueModeResolver;
	}

	public boolean accepts(final Class<?> type)
	{
		return Classes.isPrimitiveType(type);
	}

	public final DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		if (!accepts(instances.getType()))
		{
			throw new IllegalArgumentException("The primitive differ can only deal with primitive types.");
		}
		final DiffNode node = new DiffNode(parentNode, instances.getSourceAccessor(), instances.getType());
		if (shouldTreatPrimitiveDefaultsAsUnassigned(node) && instances.hasBeenAdded())
		{
			node.setState(DiffNode.State.ADDED);
		}
		else if (shouldTreatPrimitiveDefaultsAsUnassigned(node) && instances.hasBeenRemoved())
		{
			node.setState(DiffNode.State.REMOVED);
		}
		else if (!instances.areEqual())
		{
			node.setState(DiffNode.State.CHANGED);
		}
		return node;
	}

	private boolean shouldTreatPrimitiveDefaultsAsUnassigned(final DiffNode node)
	{
		return primitiveDefaultValueModeResolver.resolvePrimitiveDefaultValueMode(node) == UNASSIGNED;
	}
}
