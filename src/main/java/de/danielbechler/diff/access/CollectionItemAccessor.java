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

package de.danielbechler.diff.access;

import de.danielbechler.diff.identity.EqualsIdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategy;
import de.danielbechler.diff.selector.CollectionItemElementSelector;
import de.danielbechler.diff.selector.ElementSelector;
import de.danielbechler.util.Assert;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Daniel Bechler
 */
public class CollectionItemAccessor implements TypeAwareAccessor, Accessor
{
	private final Object referenceItem;
	private final IdentityStrategy identityStrategy;

	/**
	 * Default implementation uses IdentityService.EQUALS_IDENTITY_STRATEGY.
	 *
	 * @param referenceItem
	 */
	public CollectionItemAccessor(final Object referenceItem)
	{
		this(referenceItem, EqualsIdentityStrategy.getInstance());
	}

	/**
	 * Allows for custom IdentityStrategy.
	 *
	 * @param referenceItem
	 * @param identityStrategy
	 */
	public CollectionItemAccessor(final Object referenceItem,
								  final IdentityStrategy identityStrategy)
	{
		Assert.notNull(identityStrategy, "identityStrategy");
		this.referenceItem = referenceItem;
		this.identityStrategy = identityStrategy;
	}

	public Class<?> getType()
	{
		return referenceItem != null ? referenceItem.getClass() : null;
	}

	@Override
	public String toString()
	{
		return "collection item " + getElementSelector();
	}

	public ElementSelector getElementSelector()
	{
		final CollectionItemElementSelector selector = new CollectionItemElementSelector(referenceItem);
		return identityStrategy == null ? selector : selector.copyWithIdentityStrategy(identityStrategy);
	}

	public Object get(final Object target)
	{
		final Collection targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return null;
		}
		for (final Object item : targetCollection)
		{
			if (item != null && identityStrategy.equals(item, referenceItem))
			{
				return item;
			}
		}
		return null;
	}

	public void set(final Object target, final Object value)
	{
		final Collection<Object> targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return;
		}
		final Object previous = get(target);
		if (previous != null)
		{
			unset(target);
		}
		targetCollection.add(value);
	}

	@SuppressWarnings("unchecked")
	private static Collection<Object> objectAsCollection(final Object object)
	{
		if (object == null)
		{
			return null;
		}
		else if (object instanceof Collection)
		{
			return (Collection<Object>) object;
		}
		throw new IllegalArgumentException(object.getClass().toString());
	}

	public void unset(final Object target)
	{
		final Collection<?> targetCollection = objectAsCollection(target);
		if (targetCollection == null)
		{
			return;
		}
		final Iterator<?> iterator = targetCollection.iterator();
		while (iterator.hasNext())
		{
			final Object item = iterator.next();
			if (item != null && identityStrategy.equals(item, referenceItem))
			{
				iterator.remove();
				break;
			}
		}
	}
}
