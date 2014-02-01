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

import de.danielbechler.diff.*;
import de.danielbechler.diff.annotation.*;
import de.danielbechler.util.*;
import de.danielbechler.util.Collections;

import java.beans.*;
import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;

/**
 * Resolves the accessors of a given type by using the standard Java {@link Introspector}.
 *
 * @author Daniel Bechler
 */
public class StandardBeanIntrospector implements de.danielbechler.diff.Introspector
{
	private static PropertyAwareAccessor handlePropertyDescriptor(final PropertyDescriptor descriptor)
	{
		if (shouldSkip(descriptor))
		{
			return null;
		}

		final String propertyName = descriptor.getName();
		final Method readMethod = descriptor.getReadMethod();
		final Method writeMethod = descriptor.getWriteMethod();

		final BeanPropertyAccessor accessor = new BeanPropertyAccessor(propertyName, readMethod, writeMethod);

		handleObjectDiffPropertyAnnotation(readMethod, accessor);
		handleEqualsOnlyTypeAnnotation(readMethod, accessor);

		return accessor;
	}

	private static void handleEqualsOnlyTypeAnnotation(final Method readMethod,
													   final BeanPropertyAccessor propertyAccessor)
	{
		final Class<?> returnType = readMethod.getReturnType();
		final ObjectDiffEqualsOnlyType annotation = returnType.getAnnotation(ObjectDiffEqualsOnlyType.class);
		if (annotation != null)
		{
			final EqualsOnlyComparisonStrategy comparisonStrategy;
			if (Strings.hasText(annotation.valueProviderMethod()))
			{
				comparisonStrategy = new EqualsOnlyComparisonStrategy(annotation.valueProviderMethod());
			}
			else
			{
				comparisonStrategy = new EqualsOnlyComparisonStrategy();
			}
			propertyAccessor.setComparisonStrategy(comparisonStrategy);
		}
	}

	private static void handleObjectDiffPropertyAnnotation(final Method readMethod,
														   final BeanPropertyAccessor propertyAccessor)
	{
		final ObjectDiffProperty annotation = readMethod.getAnnotation(ObjectDiffProperty.class);
		if (annotation != null)
		{
			if (annotation.equalsOnly())
			{
				final String equalsOnlyValueProviderMethod = annotation.equalsOnlyValueProviderMethod();
				final EqualsOnlyComparisonStrategy comparisonStrategy;
				if (Strings.hasText(equalsOnlyValueProviderMethod))
				{
					comparisonStrategy = new EqualsOnlyComparisonStrategy(equalsOnlyValueProviderMethod);
				}
				else
				{
					comparisonStrategy = new EqualsOnlyComparisonStrategy();
				}
				propertyAccessor.setComparisonStrategy(comparisonStrategy);
			}
			propertyAccessor.setExcluded(annotation.excluded());
			propertyAccessor.setCategories(Collections.setOf(annotation.categories()));
		}
	}

	private static boolean shouldSkip(final PropertyDescriptor descriptor)
	{
		return descriptor.getName().equals("class") || descriptor.getReadMethod() == null;
	}

	public Iterable<PropertyAwareAccessor> introspect(final Class<?> type)
	{
		Assert.notNull(type, "type");
		try
		{
			return internalIntrospect(type);
		}
		catch (IntrospectionException e)
		{
			throw Exceptions.escalate(e);
		}
	}

	private Iterable<PropertyAwareAccessor> internalIntrospect(final Class<?> type) throws IntrospectionException
	{
		final PropertyDescriptor[] descriptors = getBeanInfo(type).getPropertyDescriptors();
		final Collection<PropertyAwareAccessor> accessors = new ArrayList<PropertyAwareAccessor>(descriptors.length);
		for (final PropertyDescriptor descriptor : descriptors)
		{
			final PropertyAwareAccessor accessor = handlePropertyDescriptor(descriptor);
			if (accessor != null)
			{
				accessors.add(accessor);
			}
		}
		return accessors;
	}

	protected BeanInfo getBeanInfo(final Class<?> type) throws IntrospectionException
	{
		return Introspector.getBeanInfo(type);
	}
}
