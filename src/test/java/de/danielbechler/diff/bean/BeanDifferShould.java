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

package de.danielbechler.diff.bean;

import de.danielbechler.diff.ComparisonStrategy;
import de.danielbechler.diff.ComparisonStrategyResolver;
import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.DifferDispatcher;
import de.danielbechler.diff.Instances;
import de.danielbechler.diff.Introspector;
import de.danielbechler.diff.IntrospectorResolver;
import de.danielbechler.diff.IsIntrospectableResolver;
import de.danielbechler.diff.IsReturnableResolver;
import de.danielbechler.diff.PropertyAwareAccessor;
import de.danielbechler.diff.RootAccessor;
import org.fest.assertions.api.Assertions;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

import static de.danielbechler.diff.NodePath.buildRootPath;
import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static de.danielbechler.diff.helper.NodeMatchers.node;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.same;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Daniel Bechler
 */
public class BeanDifferShould
{
	private BeanDiffer beanDiffer;

	@Mock
	private DifferDispatcher differDispatcher;
	@Mock
	private Introspector introspector;
	@Mock
	private IntrospectorResolver introspectorResolver;
	@Mock
	private Instances instances;
	@Mock
	private IsIntrospectableResolver introspectableResolver;
	@Mock
	private IsReturnableResolver returnableResolver;
	@Mock
	private ComparisonStrategyResolver comparisonStrategyResolver;
	@Mock
	private ComparisonStrategy comparisonStrategy;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);

		when(instances.getSourceAccessor()).thenReturn(RootAccessor.getInstance());
		when(instances.getType()).then(new Answer<Object>()
		{
			public Object answer(final InvocationOnMock invocation) throws Throwable
			{
				return String.class;
			}
		});
		when(introspectorResolver.introspectorForNode(any(DiffNode.class))).thenReturn(introspector);
		when(introspector.introspect(any(Class.class))).thenReturn(Collections.<PropertyAwareAccessor>emptyList());

		beanDiffer = new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, comparisonStrategyResolver, introspectorResolver);
	}

	@DataProvider
	public static Object[][] acceptableTypes()
	{
		return new Object[][]{
				new Class[]{String.class},
				new Class[]{Object.class},
				new Class[]{Number.class},
				new Class[]{Date.class},
		};
	}

	@DataProvider
	public static Object[][] rejectableTypes()
	{
		return new Object[][]{
				new Class[]{int.class},
				new Class[]{int[].class},
				new Class[]{boolean.class}
		};
	}

	@Test(dataProvider = "acceptableTypes")
	public void accept_all_object_types(final Class<?> type)
	{
		Assertions.assertThat(beanDiffer.accepts(type)).isTrue();
	}

	@Test(dataProvider = "rejectableTypes")
	public void reject_primitive_types(final Class<?> type)
	{
		Assertions.assertThat(beanDiffer.accepts(type)).isFalse();
	}

	@Test
	public void return_untouched_node_if_working_and_base_are_null()
	{
		when(instances.areNull()).thenReturn(true);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node).self().isUntouched();
	}

	@Test
	public void return_added_node_if_working_is_not_null_and_base_is()
	{
		when(instances.hasBeenAdded()).thenReturn(true);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node.getState(), is(DiffNode.State.ADDED));
	}

	@Test
	public void return_removed_node_if_working_is_null_and_base_is_not()
	{
		when(instances.hasBeenRemoved()).thenReturn(true);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node.getState(), is(DiffNode.State.REMOVED));
	}

	@Test
	public void return_untouched_node_if_working_and_base_are_the_same_instance()
	{
		when(instances.areNull()).thenReturn(true);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		assertThat(node).self().isUntouched();
	}

	@Test
	public void compare_via_comparisonStrategy_if_present()
	{
		when(comparisonStrategyResolver.resolveComparisonStrategy(node(buildRootPath()))).thenReturn(comparisonStrategy);

		beanDiffer.compare(DiffNode.ROOT, instances);

		verify(comparisonStrategy).compare(node(buildRootPath()), same(instances));
	}

	@Test
	public void compare_via_introspector_if_introspectable()
	{
		when(introspectableResolver.isIntrospectable(node(buildRootPath()))).thenReturn(true);

		beanDiffer.compare(DiffNode.ROOT, instances);

		verify(introspector).introspect(instances.getType());
	}

	@Test
	public void delegate_comparison_of_properties_when_comparing_via_introspector()
	{
		given_root_node_is_introspectable();
		final PropertyAwareAccessor propertyAccessor = given_introspector_returns_PropertyAccessor("foo");
		final DiffNode propertyNode = given_DifferDispatcher_returns_Node_for_PropertyAccessor(propertyAccessor);
		given_Node_is_returnable(propertyNode);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		Assertions.assertThat(node.getChild("foo")).isSameAs(propertyNode);
	}

	@Test
	public void do_not_add_property_nodes_to_bean_node_if_they_are_not_returnable()
	{
		given_root_node_is_introspectable();
		final PropertyAwareAccessor propertyAccessor = given_introspector_returns_PropertyAccessor("foo");
		final DiffNode propertyNode = given_DifferDispatcher_returns_Node_for_PropertyAccessor(propertyAccessor);
		given_Node_is_not_returnable(propertyNode);

		final DiffNode node = beanDiffer.compare(DiffNode.ROOT, instances);

		Assertions.assertThat(node.hasChildren()).isFalse();
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_DifferDispatcher()
	{
		new BeanDiffer(null, introspectableResolver, returnableResolver, comparisonStrategyResolver, introspectorResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_IntrospectorResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, comparisonStrategyResolver, null);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_introspectableResolver()
	{
		new BeanDiffer(differDispatcher, null, returnableResolver, comparisonStrategyResolver, introspectorResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_returnableResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, null, comparisonStrategyResolver, introspectorResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_comparisonStrategyResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, null, introspectorResolver);
	}

	private void given_Node_is_returnable(final DiffNode node)
	{
		doReturn(true).when(returnableResolver).isReturnable(node);
	}

	private void given_Node_is_not_returnable(final DiffNode node)
	{
		doReturn(false).when(returnableResolver).isReturnable(node);
	}

	private DiffNode given_DifferDispatcher_returns_Node_for_PropertyAccessor(final PropertyAwareAccessor propertyAccessor)
	{
		final DiffNode propertyNode = new DiffNode(propertyAccessor, String.class);
		doReturn(propertyNode).when(differDispatcher)
				.dispatch(any(DiffNode.class), any(Instances.class), same(propertyAccessor));
		return propertyNode;
	}

	private void given_root_node_is_introspectable()
	{
		doReturn(true).when(introspectableResolver).isIntrospectable(node(buildRootPath()));
	}

	private PropertyAwareAccessor given_introspector_returns_PropertyAccessor(final String propertyName)
	{
		final BeanPropertyElement propertyElement = new BeanPropertyElement(propertyName);
		final PropertyAwareAccessor propertyAccessor = mock(PropertyAwareAccessor.class);
		when(propertyAccessor.getPathElement()).thenReturn(propertyElement);
		final Set<PropertyAwareAccessor> propertyAccessors = Collections.singleton(propertyAccessor);
		when(introspector.introspect(any(Class.class))).thenReturn(propertyAccessors);
		return propertyAccessor;
	}
}
