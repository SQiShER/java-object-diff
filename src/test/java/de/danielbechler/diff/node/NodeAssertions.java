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

import de.danielbechler.diff.path.*;
import org.fest.assertions.api.*;
import org.fest.assertions.core.*;

/** @author Daniel Bechler */
public class NodeAssertions
{
	public static Syntax.SelectNode assertThat(final Node node)
	{
		return new NodeAssertLanguage(node);
	}

	public static final class NodeAssertLanguage implements Syntax.SelectNode, Syntax.AssertNode
	{
		private final Node rootNode;

		private Node selectedNode;
		private PropertyPath propertyPath;

		private NodeAssertLanguage(final Node rootNode)
		{
			this.rootNode = rootNode;
		}

		@Override
		public Syntax.AssertNode node()
		{
			this.selectedNode = rootNode;
			this.propertyPath = new PropertyPathBuilder().withRoot().build();
			return this;
		}

		@Override
		public Syntax.AssertNode child(final PropertyPath propertyPath)
		{
			if (rootNode == null)
			{
				this.selectedNode = null;
			}
			else
			{
				this.selectedNode = this.rootNode.getChild(propertyPath);
			}
			this.propertyPath = propertyPath;
			return this;
		}

		@Override
		public Syntax.AssertNode child(final PropertyPathBuilder propertyPathBuilder)
		{
			return child(propertyPathBuilder.build());
		}

		@Override
		public Syntax.AssertNode child(final String... propertyNames)
		{
			return child(PropertyPath.with(propertyNames));
		}

		@Override
		public Syntax.AssertNode doesExist()
		{
			Assertions.assertThat(selectedNode)
					  .describedAs("Expected a child at path " + propertyPath + ", but it's missing.")
					  .isNotNull();
			return this;
		}

		@Override
		public Syntax.AssertNode doesNotExist()
		{
			Assertions.assertThat(selectedNode)
					  .describedAs("Expected no child at path " + propertyPath + ", but found " + selectedNode)
					  .isNull();
			return this;
		}

		@Override
		public Syntax.AssertNode hasState(final Node.State state)
		{
			doesExist();
			Assertions.assertThat(selectedNode.getState()).isEqualTo(state);
			return this;
		}

		@Override
		public Syntax.AssertNode hasChildren()
		{
			doesExist();
			return this;
		}

		@Override
		public Syntax.AssertNode hasChildren(final int count)
		{
			doesExist();
			Assertions.assertThat(selectedNode).has(exactChildCountOf(count));
			return this;
		}

		private static Condition<Node> exactChildCountOf(final int count)
		{
			if (count < 0)
			{
				throw new IllegalArgumentException("The number of expected children must be greater or equal to 0.");
			}
			return new Condition<Node>(count + " children")
			{
				@Override
				public boolean matches(final Node value)
				{
					return value.getChildren().size() == count;
				}
			};
		}

		@Override
		public Syntax.AssertNode hasNoChildren()
		{
			return hasChildren(0);
		}
	}

	private NodeAssertions()
	{
	}

	private interface Syntax
	{
		public interface SelectNode
		{
			AssertNode node();

			AssertNode child(PropertyPath propertyPath);

			AssertNode child(PropertyPathBuilder propertyPathBuilder);

			AssertNode child(String... propertyPathElements);
		}

		public interface AssertNode
		{
			AssertNode doesExist();

			AssertNode doesNotExist();

			AssertNode hasState(Node.State state);

			AssertNode hasChildren();

			AssertNode hasChildren(int count);

			AssertNode hasNoChildren();
		}
	}
}
