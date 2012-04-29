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

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import org.junit.*;
import org.mockito.*;

import static org.hamcrest.core.Is.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/** @author Daniel Bechler */
public class ConfigurationTest
{
	@Mock
	private TypeAwareNode node;

	private Configuration configuration;

	@Before
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		configuration = new Configuration();
	}

	@Test
	public void testIsIgnored() throws Exception
	{

	}

	@Test
	public void testIsIncluded() throws Exception
	{

	}

	@Test
	public void testIsExcluded() throws Exception
	{

	}

	@Test
	public void testIsEqualsOnly() throws Exception
	{

	}

	@Test
	public void testIsReturnable() throws Exception
	{

	}

	@Test
	public void testIsIgnoredWithConfiguredPropertyPath() throws Exception
	{
		final PropertyPath propertyPath = PropertyPath.with("value");
		when(node.getPropertyPath()).thenReturn(propertyPath);
		configuration.withoutProperty(propertyPath);
		assertThat(configuration.isIgnored(node), is(true));
	}

	@Test
	public void testIsIgnoredWithPropertyThatShouldNotBeIgnored() throws Exception
	{
		assertThat(configuration.isIgnored(node), is(false));
	}

	@Test
	public void testIsIgnoredWithCategory() throws Exception
	{
		when(node.getCategories()).thenReturn(java.util.Collections.singleton("foo"));
		assertThat(configuration.isIgnored(node), is(false));
		configuration.withoutCategory("foo");
		assertThat(configuration.isIgnored(node), is(true));
	}

	@Test
	public void testIsEqualsOnlyWithConfiguredPropertyPath() throws Exception
	{
		final PropertyPath propertyPath = PropertyPath.with("value");
		when(node.getPropertyPath()).thenReturn(propertyPath);
		configuration.withEqualsOnlyProperty(propertyPath);
		assertThat(configuration.isEqualsOnly(node), is(true));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testIsEqualsOnlyWithConfiguredPropertyType() throws Exception
	{
		final Class aClass = ObjectWithString.class;
		when(node.getPropertyType()).thenReturn(aClass);
		configuration.withEqualsOnlyType(aClass);
		assertThat(configuration.isEqualsOnly(node), is(true));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testIsEqualsOnlyWithSimpleType() throws Exception
	{
		final Class aClass = String.class;
		when(node.getPropertyType()).thenReturn(aClass);
		assertThat(configuration.isEqualsOnly(node), is(true));
	}

	@Test
	public void testIsEqualsOnlyWithAccessorFlag() throws Exception
	{
		when(node.isEqualsOnly()).thenReturn(true);
		assertThat(configuration.isEqualsOnly(node), is(true));
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void testEqualsOnlyWithTypeThatShouldNotBeComparedUsingEquals() throws Exception
	{
		final Class aClass = ObjectWithCollection.class;
		when(node.getPropertyType()).thenReturn(aClass);
		assertThat(configuration.isEqualsOnly(node), is(false));
	}
}
