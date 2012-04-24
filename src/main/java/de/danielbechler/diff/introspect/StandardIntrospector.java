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

/** @author Daniel Bechler */
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
