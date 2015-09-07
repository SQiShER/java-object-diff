/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.RootAccessor;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.identity.EqualsIdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategy;
import de.danielbechler.diff.identity.IdentityStrategyResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.path.NodePath;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static de.danielbechler.diff.helper.NodeMatchers.collectionItemAccessor;
import static de.danielbechler.diff.helper.NodeMatchers.node;
import static de.danielbechler.diff.helper.NodeMatchers.state;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Daniel Bechler
 */
public class CollectionDifferShould
{
	private final IdentityStrategyResolver identityStrategyResolver = new IdentityStrategyResolver()
	{
		public IdentityStrategy resolveIdentityStrategy(final DiffNode node)
		{
			return EqualsIdentityStrategy.getInstance();
		}

	};
	@Mock
	private ComparisonStrategyResolver comparisonStrategyResolver;
	@Mock
	private ComparisonStrategy comparisonStrategy;
	@Mock
	private DifferDispatcher differDispatcher;
	@Mock
	private Instances instances;

	private CollectionDiffer collectionDiffer;
	private DiffNode node;
	private Collection<String> baseCollection;
	private Collection<String> workingCollection;

	@BeforeMethod
	public void setUp() throws Exception
	{
		initMocks(this);
		collectionDiffer = new CollectionDiffer(differDispatcher,
				comparisonStrategyResolver, identityStrategyResolver);
		baseCollection = new HashSet<String>();
		workingCollection = new HashSet<String>();
		when(instances.getSourceAccessor()).thenReturn(
				RootAccessor.getInstance());
		when(instances.getType()).thenAnswer(new Answer<Object>()
		{
			public Object answer(final InvocationOnMock invocation)
					throws Throwable
			{
				return List.class;
			}
		});
		when(instances.getBase(Collection.class)).thenReturn(baseCollection);
		when(instances.getWorking(Collection.class)).thenReturn(
				workingCollection);
	}

	@Test(dataProviderClass = DifferAcceptTypeDataProvider.class, dataProvider = "collectionTypes")
	public void accept_all_collection_types(final Class<?> type)
	{
		assertThat(collectionDiffer.accepts(type)).as(
				"accepts(" + type.getSimpleName() + ")").isTrue();
	}

	@Test(dataProviderClass = DifferAcceptTypeDataProvider.class, dataProvider = "beanTypes")
	public void not_accept_non_collection_types(final Class<?> type)
	{
		assertThat(collectionDiffer.accepts(type)).as(
				"accepts(" + type.getSimpleName() + ")").isFalse();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_DifferDispatcher()
	{
		new CollectionDiffer(null, comparisonStrategyResolver,
				identityStrategyResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_ComparisonStrategyResolver()
	{
		new CollectionDiffer(differDispatcher, null, identityStrategyResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_IdentityStrategyResolver()
	{
		new CollectionDiffer(differDispatcher, comparisonStrategyResolver, null);
	}

	@Test
	public void return_untouched_node_when_instances_are_same()
	{
		given_instances_are_same();

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node).has(state(DiffNode.State.UNTOUCHED));
	}

	private void given_instances_are_same()
	{
		when(instances.areSame()).thenReturn(true);
	}

	@Test
	public void return_added_node_when_instance_has_been_added()
			throws Exception
	{
		given_instance_has_been_added();

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node).has(state(DiffNode.State.ADDED));
	}

	private void given_instance_has_been_added()
	{
		when(instances.hasBeenAdded()).thenReturn(true);
	}

	@Test
	public void delegate_added_items_to_dispatcher_when_instance_has_been_added()
	{
		final String addedItem = "foo";
		given_instance_has_been_added();
		given_instance_has_added_item(addedItem);

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(differDispatcher).dispatch(node(NodePath.withRoot()),
				same(instances), collectionItemAccessor(addedItem));
		verifyNoMoreInteractions(differDispatcher);
	}

	private void given_instance_has_added_item(final String item)
	{
		baseCollection.remove(item);
		workingCollection.add(item);
	}

	@Test
	public void return_removed_node_when_instance_has_been_removed()
			throws Exception
	{
		given_instance_has_been_removed();

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node).has(state(DiffNode.State.REMOVED));
	}

	private void given_instance_has_been_removed()
	{
		when(instances.hasBeenRemoved()).thenReturn(true);
	}

	@Test
	public void delegate_removed_items_to_dispatcher_when_instance_has_been_removed()
	{
		final String removedItem = "foo";
		given_instance_has_been_removed();
		given_instance_has_removed_item(removedItem);

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(differDispatcher).dispatch(node(NodePath.withRoot()),
				same(instances), collectionItemAccessor(removedItem));
		verifyNoMoreInteractions(differDispatcher);
	}

	private void given_instance_has_removed_item(final String item)
	{
		baseCollection.add(item);
		workingCollection.remove(item);
	}

	@Test
	public void compare_using_comparison_strategy_if_available()
	{
		given_a_comparison_strategy_can_be_resolved();

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(comparisonStrategy, atLeastOnce()).compare(
				node(NodePath.withRoot()), same(List.class),
				eq(workingCollection), eq(baseCollection));
	}

	private void given_a_comparison_strategy_can_be_resolved()
	{
		when(
				comparisonStrategyResolver
						.resolveComparisonStrategy(any(DiffNode.class)))
				.thenReturn(comparisonStrategy);
	}

	@Test
	public void delegate_added_items_to_dispatcher_when_performaing_deep_comparison()
	{
		final String addedItem = "added";
		given_instance_has_added_item(addedItem);

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(differDispatcher).dispatch(node(NodePath.withRoot()),
				same(instances), collectionItemAccessor(addedItem));
		verifyNoMoreInteractions(differDispatcher);
	}

	@Test
	public void delegate_removed_items_to_dispatcher_when_performaing_deep_comparison()
	{
		final String removedItem = "removed";
		given_instance_has_removed_item(removedItem);

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(differDispatcher).dispatch(node(NodePath.withRoot()),
				same(instances), collectionItemAccessor(removedItem));
		verifyNoMoreInteractions(differDispatcher);
	}

	@Test
	public void delegate_known_items_to_dispatcher_when_performaing_deep_comparison()
	{
		final String knownItem = "known";
		given_instance_has_known_item(knownItem);

		node = collectionDiffer.compare(DiffNode.ROOT, instances);

		verify(differDispatcher).dispatch(node(NodePath.withRoot()),
				same(instances), collectionItemAccessor(knownItem));
		verifyNoMoreInteractions(differDispatcher);
	}

	private void given_instance_has_known_item(final String item)
	{
		baseCollection.add(item);
		workingCollection.add(item);
	}
}
