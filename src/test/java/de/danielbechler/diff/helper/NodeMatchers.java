/*
 * Copyright 2013 Daniel Bechler
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

package de.danielbechler.diff.helper;

import de.danielbechler.diff.*;
import de.danielbechler.diff.collection.*;
import org.fest.assertions.core.*;
import org.mockito.*;

import static org.mockito.Matchers.*;

/** @author Daniel Bechler */
public class NodeMatchers
{
	private NodeMatchers()
	{
	}

	public static Accessor collectionItemAccessor(final Object item)
	{
		return argThat(new ArgumentMatcher<Accessor>()
		{
			@Override
			public boolean matches(final Object argument)
			{
				if (argument != null && argument instanceof CollectionItemAccessor)
				{
					@SuppressWarnings("TypeMayBeWeakened")
					final CollectionItemAccessor accessor = (CollectionItemAccessor) argument;
					final CollectionElement collectionElement = new CollectionElement(item);
					return accessor.getPathElement().equals(collectionElement);
				}
				return false;
			}
		});
	}

	public static DiffNode node(final NodePath nodePath, final Class<?> typeHint)
	{
		final NodeMatcher node = new NodeMatcher();
		node.expectedNodePath = nodePath;
		node.expectedTypeHint = typeHint;
		return argThat(node);
	}

	public static DiffNode node(final NodePath nodePath)
	{
		final NodeMatcher node = new NodeMatcher();
		node.expectedNodePath = nodePath;
		return argThat(node);
	}

	public static Condition<DiffNode> state(final DiffNode.State state)
	{
		return new Condition<DiffNode>("state " + state.toString())
		{
			@Override
			public boolean matches(final DiffNode value)
			{
				return value.getState() == state;
			}
		};
	}

	private static class NodeMatcher extends ArgumentMatcher<DiffNode>
	{
		private Class<?> expectedTypeHint;
		private NodePath expectedNodePath;

		@Override
		public boolean matches(final Object argument)
		{
			if (argument instanceof DiffNode)
			{
				final DiffNode node = (DiffNode) argument;
				final NodePath path = node.getPath();
				if (expectedNodePath != null && path != null && !path.matches(this.expectedNodePath))
				{
					return false;
				}
				if (expectedTypeHint != null && expectedTypeHint != node.getValueType())
				{
					return false;
				}
				return true;
			}
			return false;
		}
	}
}
