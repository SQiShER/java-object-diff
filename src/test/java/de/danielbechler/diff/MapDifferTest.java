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

package de.danielbechler.diff;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.node.NodeAssertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.hamcrest.core.IsEqual.*;
import static org.hamcrest.core.IsNull.*;

/** @author Daniel Bechler */
public class MapDifferTest
{
	private MapDiffer differ;

	@BeforeMethod
	public void setUp()
	{
		differ = new MapDiffer();
	}

	@Test
	public void testWithAdditionOfSimpleTypeToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode node = differ.compare(working, base);
		assertThat(node.isMapNode(), is(true));
		assertThat(node.hasChildren(), is(true));
		assertThat(node.getState(), is(Node.State.CHANGED));

		final Collection<Node> children = node.getChildren();
		assertThat(children.size(), is(1));

		final Node child = children.iterator().next();
		assertThat((String) child.get(working), equalTo("bar"));
		assertThat(child.get(base), nullValue());
		assertThat(child.getState(), is(Node.State.ADDED));
	}

	@Test
	public void testWithNewMapInWorkingAndNoneInBase()
	{
		final Map<String, String> base = null;
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode node = differ.compare(working, base);
		assertThat(node.getState(), is(Node.State.ADDED));

		final Collection<Node> children = node.getChildren();
		assertThat(children.size(), is(1));

		final Node child = children.iterator().next();
		assertThat(child.getState(), is(Node.State.ADDED));
	}

	@Test
	public void testWithSameEntryInBaseAndWorking()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = new TreeMap<String, String>();
		working.put("foo", "bar");

		final MapNode node = differ.compare(working, base);
		assertThat(node.getState(), is(Node.State.UNTOUCHED));
		assertThat(node.hasChildren(), is(false));
	}

	@Test
	public void testWithSingleEntryAddedToWorkingMap()
	{
		final Map<String, String> base = new TreeMap<String, String>();
		base.put("foo", "bar");
		final Map<String, String> working = null;

		final MapNode node = differ.compare(working, base);
		assertThat(node.getState(), is(Node.State.REMOVED));

		final Collection<Node> children = node.getChildren();
		assertThat(children.size(), is(1));

		final Node child = children.iterator().next();
		assertThat(child.getState(), is(Node.State.REMOVED));
	}

	@Test
	public void testWithoutMapInBaseAndWorking()
	{
		final MapNode node = differ.compare((Map<?, ?>) null, null);
		assertThat(node.getState(), is(Node.State.UNTOUCHED));
		assertThat(node.hasChildren(), is(false));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testConstructionWithoutDelegator()
	{
		new MapDiffer(null);
	}

	@Test
	public void testConstructionWithDelegator()
	{
		// just for the coverage
		new MapDiffer(new DelegatingObjectDifferImpl());
	}

	/**
	 * Ensures that the map can handle null values in both, the base and the working version, in which case no
	 * type can be detected.
	 */
	@Test
	public void testWithAllNullMapItem()
	{
		final Map<String, String> working = new HashMap<String, String>(1);
		working.put("foo", null);

		final Map<String, String> base = new HashMap<String, String>(1);
		base.put("foo", null);

		final MapNode node = differ.compare(working, base);
		assertThat(node.getState(), is(Node.State.UNTOUCHED));
	}

	@Test
	public void testWithSameEntries()
	{
		final Map<String, String> modified = new LinkedHashMap<String, String>(1);
		modified.put("foo", "bar");
		final Map<String, String> base = new LinkedHashMap<String, String>(modified);
		modified.put("ping", "pong");

		final MapNode root = differ.compare(modified, base);

		assertThat(root).root().hasChildren(1);
		assertThat(root).child(PropertyPath.createBuilder().withRoot().withMapKey("foo")).doesNotExist();
		assertThat(root).child(PropertyPath.createBuilder().withRoot().withMapKey("ping"))
				.hasState(Node.State.ADDED)
				.hasNoChildren();
	}

	@Test
	public void testWithChangedEntry()
	{
		final Map<String, String> working = new LinkedHashMap<String, String>(1);
		working.put("foo", "bar");

		final Map<String, String> base = new LinkedHashMap<String, String>(1);
		base.put("foo", "woot");

		final MapNode node = differ.compare(working, base);
		assertThat("Node should have exactly one child", node.getChildren().size(), is(1));
		assertThat("Child node should have changed", node.getChildren()
														 .iterator()
														 .next()
														 .getState(), is(Node.State.CHANGED));
	}

	@Test
	public void testCompareWithDifferentMapImplementationsSucceeds() throws Exception
	{
		final Map<String, String> base = new LinkedHashMap<String, String>(Collections.singletonMap("test", "foo"));
		final Map<String, String> working = new TreeMap<String, String>(Collections.singletonMap("test", "bar"));
		final MapNode node = differ.compare(base, working);
		assertThat(node.hasChanges(), is(true));
	}
}
