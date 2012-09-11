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
import de.danielbechler.diff.path.*;
import org.hamcrest.core.*;
import org.junit.*;

import java.util.*;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class DefaultNodeTest
{
	@Mock
	private Node parentNode;
	@Mock
	private Accessor accessor;

	@Before
	public void setUp()
	{
		initMocks(this);
	}

	@Test
	public void testHasChanges_returns_false_when_untouched()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.UNTOUCHED);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_false_when_ignored()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.IGNORED);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_false_when_circular()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CIRCULAR);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_true_when_changed()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_removed()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.REMOVED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_added()
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.ADDED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_child_has_changed()
	{
		final Node root = new DefaultNode(List.class);
		final Node child = new CollectionNode(root, new CollectionItemAccessor("foo"), String.class);
		root.addChild(child);
		child.setState(Node.State.ADDED);
		Assert.assertThat(root.hasChanges(), Is.is(true));
	}

	@Test
	public void testGetPropertyPath_with_parent_node_should_return_canonical_path()
	{
		final PropertyPath.AppendableBuilder pathBuilder = PropertyPath.createBuilder().withRoot();
		when(parentNode.getPropertyPath()).thenReturn(pathBuilder.build());
		when(accessor.getPathElement()).thenReturn(new NamedPropertyElement("foo"));

		final Node root = new DefaultNode(parentNode, accessor, Object.class);

		assertThat(root.getPropertyPath()).isEqualTo(pathBuilder.withPropertyName("foo").build());
	}

	@Test
	public void testGetPropertyPath_without_parent_node_should_return_root_path()
	{
		final Node root = new DefaultNode(Object.class);
		assertThat(root.getPropertyPath()).isEqualTo(PropertyPath
				.createBuilder().withRoot().build());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddChild_throws_exception_when_root_node_is_passed() throws Exception
	{
		final Node root = new DefaultNode(Object.class);
		root.addChild(new DefaultNode(Object.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddChild_throws_exception_when_passed_node_is_already_child_of_another_node() throws Exception
	{
		final Node node1 = new DefaultNode(Object.class);
		final Node node2 = new DefaultNode(Object.class);
		final Node node3 = new DefaultNode(accessor, Object.class);
		node1.addChild(node3);
		node2.addChild(node3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddChild_throws_exception_when_node_is_added_to_itself() throws Exception
	{
		final Node node = new DefaultNode(parentNode, accessor, Object.class);
		node.addChild(node);
	}

	@Test
	public void testAddChild_establishes_parent_child_relationship() throws Exception
	{
		final Node node1 = new DefaultNode(List.class);
		final Node node2 = new DefaultNode(new CollectionItemAccessor("foo"), String.class);
		node1.addChild(node2);
		assertThat(node1.getChildren()).containsOnly(node2);
		assertThat(node2.getParentNode()).isSameAs(node1);
	}

	@Test
	public void testAddChild_changes_node_state_to_changed_if_changed_child_node_gets_added()
	{
		final Node node = new DefaultNode(Object.class);
		final Node nodeMock = mock(Node.class);
		when(nodeMock.hasChanges()).thenReturn(true);
		node.addChild(nodeMock);
		NodeAssertions.assertThat(node).node().hasState(Node.State.CHANGED);
	}
}
