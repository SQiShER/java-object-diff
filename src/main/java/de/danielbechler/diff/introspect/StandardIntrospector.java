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

package de.danielbechler.diff.introspect;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.annotation.*;
import de.danielbechler.util.*;
import de.danielbechler.util.Collections;

import java.beans.*;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.*;

/**
 * Resolves the accessors of a given type by using the standard Java {@link Introspector}.
 *
 * @author Daniel Bechler
 */
public class StandardIntrospector implements de.danielbechler.diff.introspect.Introspector
{
	public Iterable<Accessor> introspect(final Class<?> type)
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

	private Iterable<Accessor> internalIntrospect(final Class<?> type) throws IntrospectionException
	{
		final PropertyDescriptor[] descriptors = getBeanInfo(type).getPropertyDescriptors();
		final Collection<Accessor> accessors = new ArrayList<Accessor>(descriptors.length);
		for (final PropertyDescriptor descriptor : descriptors)
		{
			final PropertyAccessor accessor = handlePropertyDescriptor(descriptor);
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

	private static PropertyAccessor handlePropertyDescriptor(final PropertyDescriptor descriptor)
	{
		if (shouldSkip(descriptor))
		{
			return null;
		}

		final String propertyName = descriptor.getName();
		final Method readMethod = descriptor.getReadMethod();
		final Method writeMethod = descriptor.getWriteMethod();

		final PropertyAccessor accessor = new PropertyAccessor(propertyName, readMethod, writeMethod);

		handleObjectDiffPropertyAnnotation(readMethod, accessor);
		handleEqualsOnlyTypeAnnotation(readMethod, accessor);

		return accessor;
	}

	private static boolean shouldSkip(final PropertyDescriptor descriptor)
	{
		return descriptor.getName().equals("class") || descriptor.getReadMethod() == null;
	}

	private static void handleObjectDiffPropertyAnnotation(final Method readMethod, final PropertyAccessor propertyAccessor)
	{
		final ObjectDiffProperty annotation = readMethod.getAnnotation(ObjectDiffProperty.class);
		if (annotation != null)
		{
			propertyAccessor.setEqualsOnly(annotation.equalsOnly());
			propertyAccessor.setIgnored(annotation.ignore());
			propertyAccessor.setCategories(Collections.setOf(annotation.categories()));
		}
	}

	private static void handleEqualsOnlyTypeAnnotation(final Method readMethod, final PropertyAccessor propertyAccessor)
	{
		final ObjectDiffEqualsOnlyType annotation = readMethod.getReturnType().getAnnotation(ObjectDiffEqualsOnlyType.class);
		if (annotation != null)
		{
			propertyAccessor.setEqualsOnly(true);
		}
	}
}
