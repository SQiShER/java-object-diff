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

package de.danielbechler.diff.map;

import de.danielbechler.diff.*;
import org.mockito.Mock;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.NodeMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class MapDifferShould
{
	private MapDiffer differ;
	private DiffNode node;
	private Map<String, String> working;
	private Map<String, String> base;
	@Mock
	private DifferDispatcher differDispatcher;
	@Mock
	private Instances instances;
	@Mock
	private Instances childInstances;
	@Mock
	private DiffNode childNode;
	@Mock
	private ComparisonStrategyResolver comparisonStrategyResolver;
	@Mock
	private IsReturnableResolver returnableResolver;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		when(instances.getSourceAccessor()).thenReturn(RootAccessor.getInstance());
		when(differDispatcher.dispatch(any(DiffNode.class), same(instances), any(Accessor.class))).thenReturn(childNode);

		differ = new MapDiffer(differDispatcher, comparisonStrategyResolver, returnableResolver);

		working = new HashMap<String, String>();
		base = new HashMap<String, String>();
	}

	@Test
	public void should_delegate_to_comparison_strategy_if_one_is_returned_by_node_inspector()
	{
		when(comparisonStrategyResolver.resolveComparisonStrategy(node(NodePath.buildRootPath())))
				.thenReturn(mock(ComparisonStrategy.class));

		node = compare(working, base);

		verify(comparisonStrategyResolver, atLeast(1)).resolveComparisonStrategy(node(NodePath
				.buildRootPath()));
	}

	@Test
	public void detect_addition()
	{
		when(instances.hasBeenAdded()).thenReturn(true);

		node = compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.ADDED);
	}

	@Test
	public void detect_removal()
	{
		when(instances.hasBeenRemoved()).thenReturn(true);

		node = compare(working, base);

		NodeAssertions.assertThat(node).self().hasState(DiffNode.State.REMOVED);
	}

	@Test
	public void delegate_comparison_of_map_entries()
	{
		when(returnableResolver.isReturnable(childNode)).thenReturn(true);
		working.put("foo", "bar");

		node = compare(working, base);

		NodeAssertions.assertThat(node).self().hasChildren(1);
//		verify(node).addChild(childNode);
	}

	@Test
	public void not_add_child_nodes_if_they_are_not_returnable()
	{
		when(returnableResolver.isReturnable(childNode)).thenReturn(false);
		working.put("foo", "bar");

		node = compare(working, base);

		NodeAssertions.assertThat(node).self().hasChildren(0);
	}

	@Test
	public void detect_no_change_if_base_and_working_are_same()
	{
		when(instances.areSame()).thenReturn(true);

		node = compare(working, base);

		NodeAssertions.assertThat(node).self().isUntouched();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_when_constructed_without_delegator()
	{
		new MapDiffer(null, comparisonStrategyResolver, returnableResolver);
	}

	private DiffNode compare(final Map<?, ?> working, final Map<?, ?> base)
	{
		when(instances.getBase()).thenReturn(base);
		when(instances.getBase(Map.class)).thenReturn(base);
		when(instances.getWorking()).thenReturn(working);
		when(instances.getWorking(Map.class)).thenReturn(working);
		node = differ.compare(DiffNode.ROOT, instances);
		return node;
	}
}
