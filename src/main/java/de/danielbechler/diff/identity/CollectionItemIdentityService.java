/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.identity;

import de.danielbechler.diff.inclusion.ValueNode;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Assert;

class CollectionItemIdentityService implements IdentityStrategyResolver
{
	private final ValueNode<IdentityStrategy> nodePathIdentityStrategies;
	private final TypePropertyIdentityStrategyResolver typePropertyIdentityStrategyResolver;
	private final IdentityConfigurer identityConfigurer;
	private IdentityStrategy defaultIdentityStrategy = EqualsIdentityStrategy.getInstance();

	CollectionItemIdentityService(final IdentityConfigurer identityConfigurer)
	{
		this.identityConfigurer = identityConfigurer;
		this.nodePathIdentityStrategies = new ValueNode<IdentityStrategy>();
		this.typePropertyIdentityStrategyResolver = new TypePropertyIdentityStrategyResolver();
	}

	public IdentityStrategy resolveIdentityStrategy(final DiffNode node)
	{
		IdentityStrategy identityStrategy = typePropertyIdentityStrategyResolver.resolve(node);
		if (identityStrategy != null)
		{
			return identityStrategy;
		}
		identityStrategy = nodePathIdentityStrategies.getNodeForPath(node.getPath()).getValue();
		if (identityStrategy != null)
		{
			return identityStrategy;
		}
		return defaultIdentityStrategy;
	}

	IdentityConfigurer.OfCollectionItems ofCollectionItems(final NodePath nodePath)
	{
		return new OfCollectionItemsByNodePath(nodePath);
	}

	IdentityConfigurer.OfCollectionItems ofCollectionItems(final Class<?> type, final String propertyName)
	{
		return new OfCollectionItemsByTypeProperty(type, propertyName);
	}

	IdentityConfigurer setDefaultIdentityStrategy(final IdentityStrategy identityStrategy)
	{
		Assert.notNull(identityStrategy, "identityStrategy");
		this.defaultIdentityStrategy = identityStrategy;
		return identityConfigurer;
	}

	private class OfCollectionItemsByNodePath implements IdentityConfigurer.OfCollectionItems
	{
		private final NodePath nodePath;

		OfCollectionItemsByNodePath(final NodePath nodePath)
		{
			this.nodePath = nodePath;
		}

		public IdentityConfigurer via(final IdentityStrategy identityStrategy)
		{
			nodePathIdentityStrategies.getNodeForPath(nodePath).setValue(identityStrategy);
			return identityConfigurer;
		}
	}

	private class OfCollectionItemsByTypeProperty implements IdentityConfigurer.OfCollectionItems
	{
		private final Class<?> type;
		private final String propertyName;

		OfCollectionItemsByTypeProperty(final Class<?> type, final String propertyName)
		{
			this.type = type;
			this.propertyName = propertyName;
		}

		public IdentityConfigurer via(final IdentityStrategy identityStrategy)
		{
			typePropertyIdentityStrategyResolver.setStrategy(identityStrategy, type, propertyName);
			return identityConfigurer;
		}
	}
}
