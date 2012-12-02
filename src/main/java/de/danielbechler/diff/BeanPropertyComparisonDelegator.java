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

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
class BeanPropertyComparisonDelegator
{
	private final DifferDelegator delegator;
	private final NodeInspector nodeInspector;
	private PropertyNodeFactory propertyNodeFactory = new PropertyNodeFactory();

	public BeanPropertyComparisonDelegator(final DifferDelegator delegator, final NodeInspector nodeInspector)
	{
		Assert.notNull(delegator, "delegator");
		Assert.notNull(nodeInspector, "nodeInspector");

		this.delegator = delegator;
		this.nodeInspector = nodeInspector;
	}

	public Node compare(final Node beanNode, final Instances beanInstances, final Accessor propertyAccessor)
	{
		Assert.notNull(beanNode, "beanNode");
		Assert.notNull(beanInstances, "beanInstances");
		Assert.notNull(propertyAccessor, "propertyAccessor");

		final Node propertyNode = propertyNodeFactory.createPropertyNode(beanNode, propertyAccessor);
		if (nodeInspector.isIgnored(propertyNode))
		{
			// this check is here to prevent the invocation of the propertyAccessor of ignored properties
			propertyNode.setState(Node.State.IGNORED);
			return propertyNode;
		}
		else
		{
			return delegator.delegate(beanNode, beanInstances.access(propertyAccessor));
		}
	}

	@TestOnly
	public void setPropertyNodeFactory(final PropertyNodeFactory propertyNodeFactory)
	{
		this.propertyNodeFactory = propertyNodeFactory;
	}
}
