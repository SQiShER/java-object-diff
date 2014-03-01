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

package de.danielbechler.diff.helper;

import de.danielbechler.diff.Accessor;
import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.NodePath;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.bean.BeanPropertyElement;
import de.danielbechler.diff.mock.ObjectWithString;
import org.testng.annotations.Test;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Daniel Bechler
 */
public class NodeAssertionsTest
{
	@Test
	public void testAssertThat_node_does_exist_succeeds_when_node_exists() throws Exception
	{
		final DiffNode node = new DiffNode(String.class);
		assertThat(node).root().doesExist();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_does_exist_fails_when_node_doesnt_exist() throws Exception
	{
		assertThat(null).root().doesExist();
	}

	@Test
	public void testAssertThat_node_does_not_exist_succeeds_when_node_doesnt_exist() throws Exception
	{
		assertThat(null).root().doesNotExist();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_does_not_exist_fails_when_node_exist() throws Exception
	{
		final DiffNode node = new DiffNode(String.class);
		assertThat(node).root().doesNotExist();
	}

	@Test
	public void testAssertThat_node_has_children_succeeds_when_children_are_present() throws Exception
	{
		final DiffNode root = new DiffNode(String.class);
		final DiffNode child = new DiffNode(root, mock(Accessor.class), String.class);
		root.addChild(child);
		assertThat(root).root().hasChildren();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_has_children_fails_when_no_children_are_present() throws Exception
	{
		final DiffNode node = new DiffNode(String.class);
		assertThat(node).root().hasChildren();
	}

	@Test
	public void testAssertThat_child_at_property_names_does_exist_succeeds_when_child_exists()
	{
		final Accessor accessor = mock(Accessor.class);
		when(accessor.getPathElement()).thenReturn(new BeanPropertyElement("value"));
		final DiffNode node = new DiffNode(ObjectWithString.class);
		final DiffNode child = new DiffNode(node, accessor, String.class);
		node.addChild(child);
		assertThat(node).child("value").doesExist();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_child_at_property_names_does_exist_fails_when_child_doesnt_exist()
	{
		assertThat(null).child("value").doesExist();
	}

	@Test
	public void testAssertThat_child_at_property_path_does_exist_succeeds_when_child_exists()
	{
		final ObjectWithString working = new ObjectWithString("foo");
		final ObjectWithString base = new ObjectWithString("bar");
		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);
		assertThat(node).child(NodePath.buildWith("value")).doesExist();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_child_at_property_path_does_exist_fails_when_child_doesnt_exist()
	{
		assertThat(null).child(NodePath.buildWith("value")).doesExist();
	}

	@Test
	public void testAssertThat_child_at_property_path_builder_does_exist_succeeds_when_child_exist()
	{
		final ObjectWithString working = new ObjectWithString("foo");
		final ObjectWithString base = new ObjectWithString("bar");
		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);
		assertThat(node).child(NodePath.createBuilder().withRoot().withPropertyName("value")).doesExist();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_child_at_property_path_builder_does_exist_fails_when_child_doesnt_exist()
	{
		assertThat(null).child(NodePath.createBuilder().withRoot().withPropertyName("value")).doesExist();
	}

	@Test
	public void testAssertThat_node_has_no_children_succeeds_when_node_has_no_children()
	{
		final DiffNode node = new DiffNode(String.class);
		assertThat(node).root().hasNoChildren();
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_has_no_children_fails_when_node_has_children()
	{
		final DiffNode root = new DiffNode(String.class);
		final DiffNode child = new DiffNode(root, mock(Accessor.class), String.class);
		root.addChild(child);
		assertThat(root).root().hasNoChildren();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testAssertThat_node_has_children_with_negative_count_throws_IllegalArgumentException()
	{
		final DiffNode node = new DiffNode(String.class);
		assertThat(node).root().hasChildren(-1);
	}

	@Test
	public void testAssertThat_node_has_changed_state_succeeds_when_node_has_changed()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.CHANGED);
		assertThat(node).root().hasState(DiffNode.State.CHANGED);
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_has_changed_state_fails_when_node_has_different_state()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.UNTOUCHED);
		assertThat(node).root().hasState(DiffNode.State.CHANGED);
	}

	@Test(expectedExceptions = AssertionError.class)
	public void testAssertThat_node_has_changed_state_fails_when_node_doesnt_exist()
	{
		assertThat(null).root().hasState(DiffNode.State.CHANGED);
	}
}
