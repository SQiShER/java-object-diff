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

package de.danielbechler.diff.introspection;

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.mock.ObjectWithEqualsOnlyPropertyType;
import de.danielbechler.diff.mock.ObjectWithInheritedPropertyAnnotation;
import de.danielbechler.diff.mock.ObjectWithPropertyAnnotations;
import de.danielbechler.diff.mock.ObjectWithString;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.util.Collections;
import org.hamcrest.core.IsEqual;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Daniel Bechler
 */
public class StandardBeanIntrospectorTest
{
	private StandardBeanIntrospector introspector;

	@BeforeMethod
	public void setUp() throws Exception
	{
		introspector = new StandardBeanIntrospector();
	}

	@Test(enabled = false)
	public void testIntrospectWithEqualsOnlyPropertyType() throws Exception
	{
		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(ObjectWithEqualsOnlyPropertyType.class);
		assertThat(accessors.iterator().hasNext(), is(true));
//		final PropertyAwareAccessor propertyAwareAccessor = accessors.iterator().next();
//		assertThat(propertyAwareAccessor.getComparisonStrategy(), instanceOf(EqualsOnlyComparisonStrategy.class));
	}

	@Test
	public void testIntrospectWithEqualsOnlyPropertyTypeAndValueProviderMethod() throws Exception
	{
		final Object object = new Object()
		{
			public ObjectWithObjectDiffEqualsOnlyTypeAnnotationAndValueProviderMethod getValue()
			{
				return null;
			}
		};

		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(object.getClass());
		assertThat(accessors.iterator().hasNext(), is(true));

		final PropertyAwareAccessor propertyAwareAccessor = accessors.iterator().next();

//		final ComparisonStrategy comparisonStrategy = propertyAwareAccessor.getComparisonStrategy();
//		assertThat(comparisonStrategy, is(instanceOf(EqualsOnlyComparisonStrategy.class)));

//		final EqualsOnlyComparisonStrategy equalsOnlyComparisonStrategy = (EqualsOnlyComparisonStrategy) comparisonStrategy;
//		assertThat(equalsOnlyComparisonStrategy.getEqualsValueProviderMethod(), is(IsEqual.equalTo("foo")));
	}

	@Test
	public void testIntrospectWithPropertyAnnotations()
	{
		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(ObjectWithPropertyAnnotations.class);
		for (final PropertyAwareAccessor accessor : accessors)
		{
			if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("ignored")))
			{
				assertThat(accessor.isExcluded(), is(true));
			}
			else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("categorized")))
			{
				assertThat(accessor.getCategories().size(), is(1));
				assertThat(accessor.getCategories(), IsEqual.equalTo(Collections.setOf("foo")));
			}
			else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("item")))
			{
				assertThat(accessor.isExcluded(), is(false));
				assertThat(accessor.getCategories().isEmpty(), is(true));
			}
			else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("key")))
			{
				// no op
			}
			else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("value")))
			{
				// no op
			}
			else
			{
				org.testng.Assert.fail("Unexpected accessor: " + accessor.getElementSelector());
			}
		}
	}

	@Test
	public void testIntrospectWithInheritedPropertyAnnotations()
	{
		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(ObjectWithInheritedPropertyAnnotation.class);
		final PropertyAwareAccessor accessor = accessors.iterator().next();
		assertThat((BeanPropertyElementSelector) accessor.getElementSelector(), IsEqual.equalTo(new BeanPropertyElementSelector("value")));
		assertThat(accessor.isExcluded(), is(true));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testIntrospectWithNullType() throws Exception
	{
		introspector.introspect(null);
	}

	@Test(expectedExceptions = RuntimeException.class)
	public void testIntrospectWithSimulatedIntrospectionException() throws Exception
	{
		introspector = new StandardBeanIntrospector()
		{
			@Override
			protected BeanInfo getBeanInfo(final Class<?> type) throws IntrospectionException
			{
				throw new IntrospectionException(type.getCanonicalName());
			}
		};
		introspector.introspect(ObjectWithString.class);
	}

	@ObjectDiffEqualsOnlyType(valueProviderMethod = "foo")
	private static class ObjectWithObjectDiffEqualsOnlyTypeAnnotationAndValueProviderMethod
	{
	}
}
