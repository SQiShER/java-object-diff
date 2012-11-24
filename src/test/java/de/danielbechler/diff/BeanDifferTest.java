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
import org.mockito.Mock;
import org.testng.annotations.*;

import java.util.*;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.core.Is.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class BeanDifferTest
{
	private BeanDiffer differ;
	@Mock
	private DelegatingObjectDiffer delegate;
	@Mock
	private Introspector introspector;
	@Mock
	private Accessor accessor;
	@Mock
	private Node node;
	private Configuration configuration;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
		configuration = mock(Configuration.class);
		when(configuration.isIncluded(any(Node.class))).thenReturn(true);
		when(configuration.isReturnable(any(Node.class))).thenReturn(true);
		differ = new BeanDiffer(delegate, configuration);
		differ.setIntrospector(introspector);
	}

	@Test
	public void testCompareWithDifferentStrings() throws Exception
	{
		when(configuration.isEqualsOnly(any(Node.class))).thenReturn(true);
		final Node node = differ.compare("foo", "bar");
		assertThat(node.hasChanges(), is(true));
		assertThat(node.hasChildren(), is(false));
		assertThat(node.getState(), is(Node.State.CHANGED));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testCompareWithDifferentTypes()
	{
		differ.compare("foo", 1337);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testCompareWithoutWorkingInstance()
	{
		when(configuration.isEqualsOnly(any(Node.class))).thenReturn(true);

		differ.compare(null, "foo");
	}

	@Test
	public void testCompareWithoutAnyInstances()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), null, null));

		assertThat(node.getState(), is(Node.State.UNTOUCHED));
	}

	@Test
	public void testCompareWithIgnoredProperty()
	{
		when(configuration.isIgnored(any(Node.class))).thenReturn(true);

		assertThat(differ.compare("foo", "bar").getState(), is(Node.State.IGNORED));
	}

	@Test
	public void testCompareWithAddedInstance()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), "foo", null));

		assertThat(node.getState(), is(Node.State.ADDED));
	}

	@Test
	public void testCompareWithRemovedInstance()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), null, "foo"));

		assertThat(node.getState(), is(Node.State.REMOVED));
	}

	@Test
	public void testCompareWithSameInstance()
	{
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), "foo", "foo"));

		assertThat(node.getState(), is(Node.State.UNTOUCHED));
	}

	@Test
	public void testCompareWithEqualsOnlyTypes()
	{
		when(configuration.isEqualsOnly(any(Node.class))).thenReturn(true);

		final ObjectWithHashCodeAndEquals working = new ObjectWithHashCodeAndEquals("foo");
		final ObjectWithHashCodeAndEquals base = new ObjectWithHashCodeAndEquals("foo");
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), working, base));

		assertThat(node.getState(), is(Node.State.UNTOUCHED));
	}

	@Test
	public void testCompareWithUnequalEqualsOnlyTypes()
	{
		when(configuration.isEqualsOnly(any(Node.class))).thenReturn(true);

		final ObjectWithHashCodeAndEquals working = new ObjectWithHashCodeAndEquals("foo");
		final ObjectWithHashCodeAndEquals base = new ObjectWithHashCodeAndEquals("bar");
		final Node node = differ.compare(Node.ROOT, Instances.of(new RootAccessor(), working, base));

		assertThat(node.getState(), is(Node.State.CHANGED));
	}

//	@Test
//	public void testCompareWithEqualsOnlyPath()
//	{
//		when(delegate.isEqualsOnlyPath(any(PropertyPath.class))).thenReturn(true);
//
//		final ObjectWithHashCodeAndEquals working = new ObjectWithHashCodeAndEquals("foo", "1");
//		final ObjectWithHashCodeAndEquals base = new ObjectWithHashCodeAndEquals("bar", "1");
//		final Node node = differ.compare(working, base);
//
//		verify(delegate, atLeastOnce()).isEqualsOnlyPath(any(PropertyPath.class));
//		assertThat(node.getState(), is(Node.State.CHANGED));
//	}

	@Test
	public void testCompareWithComplexType()
	{
		when(introspector.introspect(any(Class.class))).thenReturn(Arrays.<Accessor>asList(accessor));
		when(delegate.delegate(any(Node.class), any(Instances.class))).thenReturn(node);
		when(configuration.isIntrospectible(any(Node.class))).thenReturn(true);
		when(configuration.isReturnable(any(Node.class))).thenReturn(true);
		when(node.hasChanges()).thenReturn(true);

		final Node node = differ.compare(new Object(), new Object());
		assertThat(node.getState(), is(Node.State.CHANGED));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testConstructionWithoutObjectDiffer()
	{
		new BeanDiffer(null, null);
	}

}
