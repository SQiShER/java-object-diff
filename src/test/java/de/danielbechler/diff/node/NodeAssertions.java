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
import de.danielbechler.diff.visitor.*;
import org.fest.assertions.api.*;
import org.fest.assertions.core.*;

/** @author Daniel Bechler */
public final class NodeAssertions
{
	public static Syntax.SelectNode assertThat(final Node node)
	{
		return new NodeAssertionLanguage(node);
	}

	public static final class NodeAssertionLanguage implements Syntax.SelectNode, Syntax.AssertNode
	{
		private final Node rootNode;

		private Node selectedNode;
		private PropertyPath propertyPath;

		private NodeAssertionLanguage(final Node rootNode)
		{
			this.rootNode = rootNode;
		}

		@Override
		public Syntax.AssertNode node()
		{
			this.selectedNode = rootNode;
			this.propertyPath = PropertyPath.createBuilder().withRoot().build();
			return this;
		}

		@Override
		public Syntax.AssertNode child(final PropertyPath propertyPath)
		{
			if (rootNode != null)
			{
				selectedNode = rootNode.getChild(propertyPath);
			}
			this.propertyPath = propertyPath;
			return this;
		}

		@Override
		public Syntax.AssertNode child(final PropertyPath.AppendableBuilder propertyPathBuilder)
		{
			return child(propertyPathBuilder.build());
		}

		@Override
		public Syntax.AssertNode child(final String propertyName, final String... propertyNames)
		{
			return child(PropertyPath.buildWith(propertyName, propertyNames));
		}

		@Override
		public Syntax.AssertNode doesExist()
		{
			Assertions.assertThat(rootNode).has(childAt(propertyPath));
			return this;
		}

		@Override
		public Syntax.AssertNode doesNotExist()
		{
			Assertions.assertThat(rootNode).has(noChildAt(propertyPath));
			return this;
		}

		@Override
		public Syntax.AssertNode hasState(final Node.State state)
		{
			doesExist();
			Assertions.assertThat(selectedNode).has(state(state));
			return this;
		}

		@Override
		public Syntax.AssertNode hasChildren()
		{
			doesExist();
			Assertions.assertThat(selectedNode).has(atLeastOneChild());
			return this;
		}

		@Override
		public Syntax.AssertNode hasChildren(final int count)
		{
			doesExist();
			Assertions.assertThat(selectedNode).has(exactChildCountOf(count));
			return this;
		}

		private static Condition<Node> childAt(final PropertyPath propertyPath)
		{
			return new Condition<Node>("child at path " + propertyPath)
			{
				@Override
				public boolean matches(final Node value)
				{
					if (value == null)
					{
						return false;
					}
					else
					{
						final PropertyVisitor visitor = new PropertyVisitor(propertyPath);
						value.visit(visitor);
						return visitor.getNode() != null;
					}
				}
			};
		}

		private static Condition<Node> state(final Node.State state)
		{
			return new Condition<Node>()
			{
				@Override
				public boolean matches(final Node value)
				{
					return value != null && value.getState() == state;
				}
			};
		}

		private static Condition<Node> noChildAt(final PropertyPath propertyPath)
		{
			return new Condition<Node>("no child at path " + propertyPath)
			{
				@Override
				public boolean matches(final Node value)
				{
					if (value == null)
					{
						return true;
					}
					else
					{
						final PropertyVisitor visitor = new PropertyVisitor(propertyPath);
						value.visit(visitor);
						return visitor.getNode() == null;
					}
				}
			};
		}

		private static Condition<Node> atLeastOneChild()
		{
			return new Condition<Node>("at least one child")
			{
				@Override
				public boolean matches(final Node value)
				{
					return value.hasChildren();
				}
			};
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
					if (count == 0)
					{
						return value == null || value.getChildren().isEmpty();
					}
					else
					{
						return value != null && value.getChildren().size() == count;
					}
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

			AssertNode child(PropertyPath.AppendableBuilder propertyPathBuilder);

			AssertNode child(String propertyName, String... propertyPathElements);
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
