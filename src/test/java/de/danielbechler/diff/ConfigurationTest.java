/*
 * Copyright 2012 Daniel Bechler
 *
 * This file is part of java-object-diff.
 *
 * java-object-diff is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-object-diff is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with java-object-diff.  If not, see <http://www.gnu.org/licenses/>.
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
