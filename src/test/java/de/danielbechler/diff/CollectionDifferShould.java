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
import org.fest.assertions.api.*;
import org.mockito.*;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.node.Node.State.CHANGED;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.mockito.Mockito.*;

/** @author Daniel Bechler */
public class CollectionDifferShould
{
	private CollectionDiffer collectionDiffer;

	@Mock private NodeInspector nodeInspector;
	@Mock private DifferDelegator delegator;
	@Mock private CollectionNodeFactory collectionNodeFactory;
	@Mock private CollectionNode collectionNode;
	@Mock private Node collectionItemNode;
	@Mock private Instances instances;
	@Mock private Instances itemInstances;
	@Mock private CollectionItemAccessorFactory collectionItemAccessorFactory;

	@BeforeMethod
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
		collectionDiffer = new CollectionDiffer(delegator, nodeInspector);
		collectionDiffer.setCollectionNodeFactory(collectionNodeFactory);
		collectionDiffer.setCollectionItemAccessorFactory(collectionItemAccessorFactory);
		when(collectionNodeFactory.create(Node.ROOT, instances)).thenReturn(collectionNode);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_delegator()
	{
		new CollectionDiffer(null, nodeInspector);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_configuration()
	{
		new CollectionDiffer(delegator, null);
	}

	@Test
	public void return_untouched_node_if_base_and_working_are_the_same_instance()
	{
		when(instances.areSame()).thenReturn(true);
		compare();
		verify(collectionNode).setState(Node.State.UNTOUCHED);
	}

	@Test
	public void return_added_node_if_working_is_not_null_and_base_is_null() throws Exception
	{
		when(instances.hasBeenAdded()).thenReturn(true);
		when(instances.getWorking(Collection.class)).thenReturn(emptyList());
		compare();
		verify(collectionNode).setState(Node.State.ADDED);
	}

	@Test(dependsOnMethods = "return_added_node_if_working_is_not_null_and_base_is_null")
	public void delegate_items_of_added_collection_to_delegator()
	{
		when(instances.hasBeenAdded()).thenReturn(true);
		when(instances.getWorking(Collection.class)).thenReturn(asList("foo"));
		when(instances.access(any(CollectionItemAccessor.class))).thenReturn(itemInstances); // TODO get rid of any
		when(delegator.delegate(collectionNode, itemInstances)).thenReturn(collectionItemNode);
		when(nodeInspector.isReturnable(collectionItemNode)).thenReturn(true);
		compare();
		verify(collectionNode).addChild(collectionItemNode);
	}

	@Test
	public void return_ignored_node_if_property_is_ignored() throws Exception
	{
		when(nodeInspector.isIgnored(collectionNode)).thenReturn(true);
		compare();
		verify(collectionNode).setState(Node.State.IGNORED);
	}

	@Test
	public void return_removed_node_if_working_is_null_and_base_is_not_null() throws Exception
	{
		when(instances.hasBeenRemoved()).thenReturn(true);
		when(instances.getBase(Collection.class)).thenReturn(emptyList());
		compare();
		verify(collectionNode).setState(Node.State.REMOVED);
	}

	@Test
	public void compare_only_via_equals_if_equals_only_is_enabled()
	{
		when(nodeInspector.isEqualsOnly(collectionNode)).thenReturn(true);
		when(instances.areEqual()).thenReturn(true);
		compare();
		verify(collectionNode).setState(Node.State.UNTOUCHED);
	}
	
	@Test
	public void detect_no_change_when_comparing_using_with_equals_only_value_provider_method_and_result_is_same()
	{
		when(nodeInspector.hasEqualsOnlyValueProviderMethod(collectionNode)).thenReturn(true);
		when(nodeInspector.getEqualsOnlyValueProviderMethod(collectionNode)).thenReturn("somemethod");
		when(instances.areMethodResultsEqual("somemethod")).thenReturn(true);
		compare();
		verify(collectionNode).setState(Node.State.UNTOUCHED);
	}
	
	@Test
	public void detect_change_when_comparing_using_with_equals_only_value_provider_method_and_result_is_different()
	{
		when(nodeInspector.hasEqualsOnlyValueProviderMethod(collectionNode)).thenReturn(true);
		when(nodeInspector.getEqualsOnlyValueProviderMethod(collectionNode)).thenReturn("somemethod");
		when(instances.areMethodResultsEqual("somemethod")).thenReturn(false);
		compare();
		verify(collectionNode).setState(CHANGED);
	}

	@Test
	public void detect_changes_if_equals_only_is_enabled()
	{
		when(nodeInspector.isEqualsOnly(collectionNode)).thenReturn(true);
		compare();
		verify(collectionNode).setState(Node.State.CHANGED);
	}

	private void compare()
	{
		final CollectionNode node = collectionDiffer.compare(Node.ROOT, instances);
		Assertions.assertThat(node).isSameAs(collectionNode);
	}

	@Test
	public void delegate_items_of_removed_collection_to_delegator()
	{
		when(instances.hasBeenRemoved()).thenReturn(true);
		when(instances.getBase(Collection.class)).thenReturn(asList("foo"));
		when(instances.access(any(CollectionItemAccessor.class))).thenReturn(itemInstances); // TODO get rid of any
		when(delegator.delegate(collectionNode, itemInstances)).thenReturn(collectionItemNode);
		when(nodeInspector.isReturnable(collectionItemNode)).thenReturn(true);
		compare();
		verify(collectionNode).addChild(collectionItemNode);
	}

	@Test
	public void delegate_all_items_to_delegator_on_deep_comparison()
	{
		when(instances.getWorking(Collection.class)).thenReturn(asList("foo", "bar"));
		when(instances.getBase(Collection.class)).thenReturn(asList("foo", "foobar"));

		final Node fooNode = whenDelegatorGetsCalledWithInstancesForItem("foo");
		final Node barNode = whenDelegatorGetsCalledWithInstancesForItem("bar");
		final Node foobarNode = whenDelegatorGetsCalledWithInstancesForItem("foobar");

		when(nodeInspector.isReturnable(any(Node.class))).thenReturn(true);

		compare();

		verify(collectionNode).addChild(fooNode);
		verify(collectionNode).addChild(barNode);
		verify(collectionNode).addChild(foobarNode);
		verifyNoMoreInteractions(collectionNode);
	}

	private Node whenDelegatorGetsCalledWithInstancesForItem(final String item)
	{
		final CollectionItemAccessor fooAccessor = mock(CollectionItemAccessor.class);
		when(collectionItemAccessorFactory.createAccessorForItem(item)).thenReturn(fooAccessor);

		final Instances fooInstances = mock(Instances.class);
		when(instances.access(fooAccessor)).thenReturn(fooInstances);

		final Node fooNode = mock(Node.class);
		when(delegator.delegate(collectionNode, fooInstances)).thenReturn(fooNode);

		return fooNode;
	}
}
