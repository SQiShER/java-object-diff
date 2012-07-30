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

package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.junit.*;

import static org.fest.assertions.api.Assertions.*;

/** @author Daniel Bechler */
public class ToMapPrintingVisitorTest
{
	private ToMapPrintingVisitor visitor;

	@Before
	public void setUp() throws Exception
	{
		visitor = new ToMapPrintingVisitor("foo", "bar");
	}

	@Test
	public void testConstructor_works_with_null_values()
	{
		assertThat(new ToMapPrintingVisitor(null, null)).isNotNull();
	}

	@Test
	public void testGetMessages_returns_map_with_accepted_messages() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		assertThat(visitor.getMessages()).hasSize(1).containsKey(PropertyPath.buildRootPath());
	}

	@Test
	public void testGetMessages_returns_empty_map_if_no_messages_have_been_accepted() throws Exception
	{
		assertThat(visitor.getMessages()).isEmpty();
	}

	@Test
	public void testGetMessages_returns_modifiable_map_when_messages_exist() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		assertThat(visitor.getMessages()).hasSize(1);
		visitor.getMessages().put(PropertyPath.buildWith("foo"), "bar");
		assertThat(visitor.getMessages()).hasSize(2);
	}

	@Test
	public void testGetMessages_returns_modifiable_map_when_no_messages_exist() throws Exception
	{
		assertThat(visitor.getMessages()).hasSize(0);
		visitor.getMessages().put(PropertyPath.buildWith("foo"), "bar");
		assertThat(visitor.getMessages()).hasSize(1);
	}

	@Test
	public void testGetMessage_returns_message_when_message_present() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		final PropertyPath path = PropertyPath.buildRootPath();
		assertThat(visitor.getMessage(path)).isNotEmpty();
	}

	@Test
	public void testGetMessage_returns_null_when_message_absend() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		final PropertyPath path = PropertyPath.buildWith("doesn't-exist");
		assertThat(visitor.getMessage(path)).isNull();
	}

	@Test
	public void testHasMessages_returns_true_when_messages_exist() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		assertThat(visitor.hasMessages()).isTrue();
	}

	@Test
	public void testHasMessages_returns_false_when_no_messages_exist() throws Exception
	{
		assertThat(visitor.hasMessages()).isFalse();
	}

	@Test
	public void testGetMessagesAsString_returns_line_break_separated_list_of_messages_when_messages_exist() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		assertThat(visitor.getMessagesAsString()).isEqualTo(visitor.getMessage(PropertyPath.buildRootPath()) + "\n");
	}

	@Test
	public void testGetMessagesAsString_returns_empty_string_when_no_messages_exist() throws Exception
	{
		assertThat(visitor.getMessagesAsString()).isEmpty();
	}

	@Test
	public void testToString_is_analogous_to_getMessagesAsString() throws Exception
	{
		final Node node = new DefaultNode(String.class);
		node.setState(Node.State.CHANGED);
		node.visit(visitor);
		assertThat(visitor.toString()).isEqualTo(visitor.getMessage(PropertyPath.buildRootPath()) + "\n");
	}

	@Test
	public void testClear_removes_all_messages() throws Exception
	{
		testGetMessages_returns_map_with_accepted_messages();
		visitor.clear();
		assertThat(visitor.hasMessages()).isFalse();
	}
}
