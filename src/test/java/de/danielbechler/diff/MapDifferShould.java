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

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.node.*;
import org.mockito.Mock;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.node.Node.State.*;
import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class MapDifferShould
{
	private MapDiffer differ;
	private MapNode node;
	private Map<String, String> working;
	private Map<String, String> base;
	@Mock private DifferDelegator differDelegator;
	@Mock private NodeInspector nodeInspector;
	@Mock private MapNodeFactory mapNodeFactory;
	@Mock private MapNode internalNode;
	@Mock private Instances instances;
	@Mock private Instances childInstances;
	@Mock private Node childNode;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		when(mapNodeFactory.createMapNode(Node.ROOT, instances)).thenReturn(internalNode);
		when(instances.access(any(Accessor.class))).thenReturn(childInstances);
		when(differDelegator.delegate(internalNode, childInstances)).thenReturn(childNode);

		differ = new MapDiffer(differDelegator, nodeInspector);
		differ.setMapNodeFactory(mapNodeFactory);

		working = new HashMap<String, String>();
		base = new HashMap<String, String>();
	}

	@Test
	public void ignore_given_map_if_ignorable() throws Exception
	{
		when(nodeInspector.isIgnored(internalNode)).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(IGNORED);
	}

	@Test
	public void detect_change_when_comparing_using_equals()
	{
		when(nodeInspector.isEqualsOnly(internalNode)).thenReturn(true);
		when(instances.areEqual()).thenReturn(false);

		node = compare(working, base);

		verify(node).setState(CHANGED);
	}

	@Test
	public void detect_no_change_when_comparing_using_equals()
	{
		when(nodeInspector.isEqualsOnly(internalNode)).thenReturn(true);
		when(instances.areEqual()).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(UNTOUCHED);
	}

	@Test
	public void detect_no_change_when_comparing_using_with_method_equals_and_result_is_same()
	{
		when(nodeInspector.isWithMethodEquals(internalNode)).thenReturn(true);
		when(nodeInspector.getWithMethodEqualsMethod(internalNode)).thenReturn("somemethod");
		when(instances.areMethodResultEqual("somemethod")).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(UNTOUCHED);
	}
	
	@Test
	public void detect_change_when_comparing_using_with_method_equals_and_result_is_different()
	{
		when(nodeInspector.isWithMethodEquals(internalNode)).thenReturn(true);
		when(nodeInspector.getWithMethodEqualsMethod(internalNode)).thenReturn("somemethod");
		when(instances.areMethodResultEqual("somemethod")).thenReturn(false);

		node = compare(working, base);

		verify(node).setState(CHANGED);
	}
	
	@Test
	public void detect_addition()
	{
		when(instances.hasBeenAdded()).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(ADDED);
	}

	@Test
	public void detect_removal()
	{
		when(instances.hasBeenRemoved()).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(REMOVED);
	}

	@Test
	public void delegate_comparison_of_map_entries()
	{
		when(nodeInspector.isReturnable(childNode)).thenReturn(true);
		working.put("foo", "bar");

		node = compare(working, base);

		verify(node).addChild(childNode);
	}

	@Test
	public void not_add_child_nodes_if_they_are_not_returnable()
	{
		when(nodeInspector.isReturnable(childNode)).thenReturn(false);
		working.put("foo", "bar");

		node = compare(working, base);

		verify(node, times(0)).addChild(childNode);
	}

	@Test
	public void detect_no_change_if_base_and_working_are_same()
	{
		when(instances.areSame()).thenReturn(true);

		node = compare(working, base);

		verify(node).setState(UNTOUCHED);
	}

	@Test
	public void insert_all_entries_into_the_map_node_index()
	{
		working.put("added", "");
		working.put("known", "");
		base.put("removed", "");
		base.put("known", "");

		node = compare(working, base);

		verify(internalNode, times(1)).indexKey("added");
		verify(internalNode, times(2)).indexKey("known");
		verify(internalNode, times(1)).indexKey("removed");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_when_constructed_without_delegator()
	{
		new MapDiffer(null, nodeInspector);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_when_constructed_without_node_inspector()
	{
		new MapDiffer(differDelegator, null);
	}

	private MapNode compare(final Map<?, ?> working, final Map<?, ?> base)
	{
		when(instances.getBase()).thenReturn(base);
		when(instances.getBase(Map.class)).thenReturn(base);
		when(instances.getWorking()).thenReturn(working);
		when(instances.getWorking(Map.class)).thenReturn(working);
		node = differ.compare(Node.ROOT, instances);
		assertThat(node).isEqualTo(internalNode);
		return node;
	}
}
