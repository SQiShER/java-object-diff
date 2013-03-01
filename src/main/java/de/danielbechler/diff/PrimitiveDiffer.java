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

import static de.danielbechler.diff.Configuration.PrimitiveDefaultValueMode.*;

/** @author Daniel Bechler */
class PrimitiveDiffer implements Differ<DefaultNode>
{
	private final Configuration configuration;
	private DefaultNodeFactory defaultNodeFactory = new DefaultNodeFactory();

	public PrimitiveDiffer(final Configuration configuration)
	{
		Assert.notNull(configuration, "configuration");
		this.configuration = configuration;
	}

	public final DefaultNode compare(final Node parentNode, final Instances instances)
	{
		if (!instances.getType().isPrimitive())
		{
			throw new IllegalArgumentException("The primitive differ can only deal with primitive types.");
		}
		final DefaultNode node = defaultNodeFactory.createNode(parentNode, instances);
		if (configuration.isIgnored(node))
		{
			node.setState(Node.State.IGNORED);
		}
		else if (shouldTreatPrimitiveDefaultsAsUnassigned() && instances.hasBeenAdded())
		{
			node.setState(Node.State.ADDED);
		}
		else if (shouldTreatPrimitiveDefaultsAsUnassigned() && instances.hasBeenRemoved())
		{
			node.setState(Node.State.REMOVED);
		}
		else if (!instances.areEqual())
		{
			node.setState(Node.State.CHANGED);
		}
		return node;
	}

	private boolean shouldTreatPrimitiveDefaultsAsUnassigned()
	{
		return configuration.getPrimitiveDefaultValueMode() == UNASSIGNED;
	}

	@TestOnly
	public void setDefaultNodeFactory(final DefaultNodeFactory defaultNodeFactory)
	{
		this.defaultNodeFactory = defaultNodeFactory;
	}
}
