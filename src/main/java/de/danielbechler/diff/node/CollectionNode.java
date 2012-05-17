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

package de.danielbechler.diff.node;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.visitor.*;

import java.util.*;

/** @author Daniel Bechler */
public final class CollectionNode extends DefaultNode
{
	public CollectionNode(final Node parent, final Accessor accessor, final Class<?> valueType)
	{
		super(parent, accessor, valueType);
	}

	public Accessor accessorForItem(final Object item)
	{
		return new CollectionItemAccessor(item);
	}

	public Collection<Node> getAdditions()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.ADDED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<Node> getRemovals()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.REMOVED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	public Collection<Node> getChanges()
	{
		final AbstractFilteringVisitor visitor = new StateFilteringVisitor(State.CHANGED);
		visitChildren(visitor);
		return visitor.getMatches();
	}

	@Override
	public boolean isCollectionDifference()
	{
		return true;
	}

	@Override
	public CollectionNode toCollectionDifference()
	{
		return this;
	}

}
