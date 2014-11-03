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
import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.access.RootAccessor;
import de.danielbechler.diff.comparison.ComparisonStrategy;
import de.danielbechler.diff.comparison.ComparisonStrategyResolver;
import de.danielbechler.diff.filtering.IsReturnableResolver;
import de.danielbechler.diff.introspection.IsIntrospectableResolver;
import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.diff.introspection.TypeInfoResolver;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import org.fest.assertions.api.Assertions;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static de.danielbechler.diff.helper.NodeMatchers.node;
import static de.danielbechler.diff.path.NodePath.withRoot;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.eq;
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
	private Instances instances;
	@Mock
	private IsIntrospectableResolver introspectableResolver;
	@Mock
	private IsReturnableResolver returnableResolver;
	@Mock
	private ComparisonStrategyResolver comparisonStrategyResolver;
	@Mock
	private ComparisonStrategy comparisonStrategy;
	@Mock
	private TypeInfoResolver typeInfoResolver;

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
		when(instances.getWorking()).thenReturn("foo");
		when(instances.getBase()).thenReturn("bar");
		when(typeInfoResolver.typeInfoForNode(any(DiffNode.class))).thenReturn(new TypeInfo(Class.class));

		beanDiffer = new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, comparisonStrategyResolver, typeInfoResolver);
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
		when(comparisonStrategyResolver.resolveComparisonStrategy(node(withRoot()))).thenReturn(comparisonStrategy);

		beanDiffer.compare(DiffNode.ROOT, instances);

		verify(comparisonStrategy).compare(node(withRoot()), same(String.class), eq("foo"), eq("bar"));
	}

	@Test
	public void compare_via_introspection_if_introspectable()
	{
		when(introspectableResolver.isIntrospectable(node(withRoot()))).thenReturn(true);

		beanDiffer.compare(DiffNode.ROOT, instances);

		verify(typeInfoResolver).typeInfoForNode(node(withRoot()));
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

	private void given_root_node_is_introspectable()
	{
		doReturn(true).when(introspectableResolver).isIntrospectable(node(withRoot()));
	}

	private PropertyAwareAccessor given_introspector_returns_PropertyAccessor(final String propertyName)
	{
		final BeanPropertyElementSelector propertyElement = new BeanPropertyElementSelector(propertyName);
		final PropertyAwareAccessor propertyAccessor = mock(PropertyAwareAccessor.class);
		when(propertyAccessor.getElementSelector()).thenReturn(propertyElement);
		final TypeInfo typeInfo = new TypeInfo(Class.class);
		typeInfo.addPropertyAccessor(propertyAccessor);
		when(typeInfoResolver.typeInfoForNode(any(DiffNode.class))).thenReturn(typeInfo);
		return propertyAccessor;
	}

	private DiffNode given_DifferDispatcher_returns_Node_for_PropertyAccessor(final PropertyAwareAccessor propertyAccessor)
	{
		final DiffNode propertyNode = new DiffNode(propertyAccessor, String.class);
		doReturn(propertyNode).when(differDispatcher)
				.dispatch(any(DiffNode.class), any(Instances.class), same(propertyAccessor));
		return propertyNode;
	}

	private void given_Node_is_returnable(final DiffNode node)
	{
		doReturn(true).when(returnableResolver).isReturnable(node);
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

	private void given_Node_is_not_returnable(final DiffNode node)
	{
		doReturn(false).when(returnableResolver).isReturnable(node);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_DifferDispatcher()
	{
		new BeanDiffer(null, introspectableResolver, returnableResolver, comparisonStrategyResolver, typeInfoResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_introspectableResolver()
	{
		new BeanDiffer(differDispatcher, null, returnableResolver, comparisonStrategyResolver, typeInfoResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_returnableResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, null, comparisonStrategyResolver, typeInfoResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_comparisonStrategyResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, null, typeInfoResolver);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_typeInfoResolver()
	{
		new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, comparisonStrategyResolver, null);
	}
}
