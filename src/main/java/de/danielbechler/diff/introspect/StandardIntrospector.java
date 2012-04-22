package de.danielbechler.diff.introspect;

import de.danielbechler.diff.accessor.*;
import de.danielbechler.diff.annotation.*;
import de.danielbechler.util.*;

import java.beans.*;
import java.beans.Introspector;
import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
public class StandardIntrospector implements de.danielbechler.diff.introspect.Introspector
{
	public Iterable<Accessor<?>> introspect(final Class<?> type)
	{
		try
		{
			final Collection<Accessor<?>> accessors = new LinkedList<Accessor<?>>();
			final BeanInfo beanInfo = Introspector.getBeanInfo(type);
			for (final PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors())
			{
				final String propertyName = propertyDescriptor.getName();
				if (propertyName.equals("class"))
				{
					continue;
				}

				final Method readMethod = propertyDescriptor.getReadMethod();
				if (readMethod == null || readMethod.getAnnotation(DiffIgnore.class) != null)
				{
					continue;
				}

				final Method writeMethod = propertyDescriptor.getWriteMethod();

				final PropertyAccessor<Object> propertyAccessor = new PropertyAccessor<Object>(
						propertyName,
						readMethod,
						writeMethod
				);
				final DiffCategories categories = readMethod.getAnnotation(DiffCategories.class);
				if (categories != null)
				{
					propertyAccessor.setCategories(new TreeSet<String>(Arrays.asList(categories.value())));
				}
				accessors.add(propertyAccessor);
			}
			return accessors;
		}
		catch (IntrospectionException e)
		{
			throw Exceptions.escalate(e);
		}
	}
}
