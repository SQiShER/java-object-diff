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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.danielbechler.diff.access.PropertyAwareAccessor;
import de.danielbechler.diff.instantiation.TypeInfo;
import de.danielbechler.util.Assert;
import de.danielbechler.util.Exceptions;

/**
 * Resolves the accessors of a given type by using the standard Java {@link Introspector}.
 *
 * @author Daniel Bechler
 */
public class StandardIntrospector implements de.danielbechler.diff.introspection.Introspector
{

   private static final Logger log = LoggerFactory.getLogger(StandardIntrospector.class);

	public TypeInfo introspect(final Class<?> type)
	{
		Assert.notNull(type, "type");
		try
		{
			return internalIntrospect(type);
		}
		catch (final IntrospectionException e)
		{
			throw Exceptions.escalate(e);
		}
	}

	private TypeInfo internalIntrospect(final Class<?> type) throws IntrospectionException
	{
		final TypeInfo typeInfo = new TypeInfo(type);
		final PropertyDescriptor[] descriptors = getBeanInfo(type).getPropertyDescriptors();
		for (final PropertyDescriptor descriptor : descriptors)
		{
			if (shouldSkip(descriptor))
			{
				continue;
			}
			final String propertyName = descriptor.getName();
         Field field = findField(type, propertyName);
			final Method readMethod = descriptor.getReadMethod();
			final Method writeMethod = descriptor.getWriteMethod();
         final PropertyAwareAccessor accessor = new PropertyAccessor(propertyName, field, readMethod, writeMethod);
			typeInfo.addPropertyAccessor(accessor);
		}
		return typeInfo;
	}

	protected BeanInfo getBeanInfo(final Class<?> type) throws IntrospectionException
	{
		return Introspector.getBeanInfo(type);
	}

	private static boolean shouldSkip(final PropertyDescriptor descriptor)
	{
		if (descriptor.getName().equals("class")) // Java & Groovy
		{
			return true;
		}
		if (descriptor.getName().equals("metaClass")) // Groovy
		{
			return true;
		}
		if (descriptor.getReadMethod() == null)
		{
			return true;
		}
		return false;
	}

   private Field findField(final Class<?> type, final String propertyName) throws IntrospectionException {
      String fieldName = propertyName.substring(0, 1).toLowerCase() + propertyName.substring(1);
      Class<?> clazz = type;
      while (clazz != null) {
         try {
            return clazz.getDeclaredField(fieldName);
         } catch (NoSuchFieldException e) {
         }
         clazz = clazz.getSuperclass();
      }
      log.info("No Java Bean: No such field " + fieldName + " in " + type + " class hierarchy");
      return null;
   }

}
