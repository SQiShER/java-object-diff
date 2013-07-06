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
import de.danielbechler.diff.introspect.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.mockito.Mock;
import org.testng.annotations.*;

import static de.danielbechler.diff.node.NodeAssertions.assertThat;
import static java.util.Arrays.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
// TODO Use mocked NodeInspector instead of actual implementation of Configuration
public class BeanDifferShould
{
	private BeanDiffer differ;
	private Configuration configuration;

	@Mock private DifferDelegator delegator;
	@Mock private Introspector introspector;
	@Mock private Accessor accessor;
	@Mock private Node node;
	@Mock private BeanPropertyComparisonDelegator beanPropertyComparer;
	@Mock private DefaultNodeFactory defaultNodeFactory;
	@Mock private Instances instances;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		configuration = new Configuration();
		differ = new BeanDiffer(delegator, configuration);
		differ.setIntrospector(introspector);
	}

	@Test
	public void return_untouched_node_if_working_and_base_are_null()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(null, null));

		assertThat(node).self().isUntouched();
	}

	@Test
	public void return_added_node_if_working_is_not_null_and_base_is()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of("foo", null));

		assertThat(node.getState(), is(Node.State.ADDED));
	}

	@Test
	public void return_removed_node_if_working_is_null_and_base_is_not()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(null, "foo"));

		assertThat(node.getState(), is(Node.State.REMOVED));
	}

	@Test
	public void return_untouched_node_if_working_and_base_are_the_same_instance()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of("foo", "foo"));

		assertThat(node).self().isUntouched();
	}

	@Test
	public void ignore_ignored_properties()
	{
		configuration.withoutProperty(PropertyPath.buildRootPath());

		final Node node = differ.compare(Node.ROOT, Instances.of("foo", "bar"));

		assertThat(node).self().hasState(Node.State.IGNORED);
	}

    @Test
    public void compare_bean_via_compare_to()
    {
        final ObjectWithCompareTo working = new ObjectWithCompareTo("foo", "ignore");
        final ObjectWithCompareTo base = new ObjectWithCompareTo("foo", "ignore this too");
        configuration.withCompareToOnlyType(ObjectWithCompareTo.class);

        final Node node = differ.compare(Node.ROOT, Instances.of(working, base));

        assertThat(node).self().isUntouched();
    }

	@Test
	public void compare_bean_via_equals()
	{
		final ObjectWithHashCodeAndEquals working = new ObjectWithHashCodeAndEquals("foo", "ignore");
		final ObjectWithHashCodeAndEquals base = new ObjectWithHashCodeAndEquals("foo", "ignore this too");
		configuration.withEqualsOnlyProperty(PropertyPath.buildRootPath());

		final Node node = differ.compare(Node.ROOT, Instances.of(working, base));

		assertThat(node).self().isUntouched();
	}

	@Test
	public void compare_bean_via_introspection_and_delegate_comparison_of_properties()
	{
		final Class<Object> beanType = Object.class;
		final Accessor propertyAccessor = mock(Accessor.class);
		final Instances beanInstances = mock(Instances.class);
		final DefaultNode beanNode = mock(DefaultNode.class);
		final DefaultNode propertyNode = mock(DefaultNode.class);
		final BeanPropertyComparisonDelegator beanPropertyComparer = mock(BeanPropertyComparisonDelegator.class);
		final DefaultNodeFactory defaultNodeFactory = mock(DefaultNodeFactory.class);
		final Configuration configuration = mock(Configuration.class);
		final DifferDelegator delegator = mock(DifferDelegator.class);
		final Introspector introspector = mock(Introspector.class);

		when(defaultNodeFactory.createNode(Node.ROOT, beanInstances)).thenReturn(beanNode);
		when(configuration.isIntrospectible(beanNode)).thenReturn(true);
		doReturn(beanType).when(beanInstances).getType();
		when(introspector.introspect(beanType)).thenReturn(asList(propertyAccessor));
		when(beanPropertyComparer.compare(beanNode, beanInstances, propertyAccessor)).thenReturn(propertyNode);
		when(configuration.isReturnable(propertyNode)).thenReturn(true);

		differ = new BeanDiffer(delegator, configuration);
		differ.setIntrospector(introspector);
		differ.setBeanPropertyComparisonDelegator(beanPropertyComparer);
		differ.setDefaultNodeFactory(defaultNodeFactory);

		final Node node = differ.compare(Node.ROOT, beanInstances);

		verify(node).addChild(propertyNode);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_delegator()
	{
		new BeanDiffer(null, configuration);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_construction_without_configuration()
	{
		new BeanDiffer(delegator, null);
	}
}
