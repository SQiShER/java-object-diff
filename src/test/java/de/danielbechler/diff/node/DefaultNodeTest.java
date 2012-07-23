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
import org.hamcrest.core.*;
import org.junit.*;

import java.util.*;

/** @author Daniel Bechler */
public class DefaultNodeTest
{
	@Test
	public void testHasChanges_returns_false_when_untouched()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.UNTOUCHED);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_false_when_ignored()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.IGNORED);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_false_when_circular()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.CIRCULAR);
		Assert.assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void testHasChanges_returns_true_when_changed()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.CHANGED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_removed()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.REMOVED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_added()
	{
		final Node node = new DefaultNode(null, new RootAccessor(), String.class);
		node.setState(Node.State.ADDED);
		Assert.assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void testHasChanges_returns_true_when_child_has_changed()
	{
		final Node root = new DefaultNode(null, new RootAccessor(), List.class);
		final Node child = new CollectionNode(root, new CollectionItemAccessor("foo"), String.class);
		root.addChild(child);
		child.setState(Node.State.ADDED);
		Assert.assertThat(root.hasChanges(), Is.is(true));
	}
}
