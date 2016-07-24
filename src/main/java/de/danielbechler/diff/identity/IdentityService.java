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

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;

public class IdentityService implements IdentityConfigurer, IdentityStrategyResolver
{
	private final CollectionItemIdentityService collectionItemIdentityService = new CollectionItemIdentityService(this);
	private final ObjectDifferBuilder objectDifferBuilder;

	public IdentityService(final ObjectDifferBuilder objectDifferBuilder)
	{
		this.objectDifferBuilder = objectDifferBuilder;
	}

	public OfCollectionItems ofCollectionItems(final NodePath nodePath)
	{
		return collectionItemIdentityService.ofCollectionItems(nodePath);
	}

	public OfCollectionItems ofCollectionItems(final Class<?> type, final String propertyName)
	{
		return collectionItemIdentityService.ofCollectionItems(type, propertyName);
	}

	public IdentityConfigurer setDefaultCollectionItemIdentityStrategy(final IdentityStrategy identityStrategy)
	{
		return collectionItemIdentityService.setDefaultIdentityStrategy(identityStrategy);
	}

	public IdentityStrategy resolveIdentityStrategy(final DiffNode node)
	{
		return collectionItemIdentityService.resolveIdentityStrategy(node);
	}

	public ObjectDifferBuilder and()
	{
		return objectDifferBuilder;
	}
}
