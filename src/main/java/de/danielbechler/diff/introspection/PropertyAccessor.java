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
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import de.danielbechler.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Arrays.asList;

public class PropertyAccessor implements PropertyAwareAccessor
{
	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private final String propertyName;
	private final Class<?> type;
	private final Method readMethod;
	private final Method writeMethod;

	public PropertyAccessor(final String propertyName, final Method readMethod, final Method writeMethod)
	{
		Assert.notNull(propertyName, "propertyName");
		Assert.notNull(readMethod, "readMethod");
		this.propertyName = propertyName;
		this.readMethod = makeAccessible(readMethod);
		this.writeMethod = makeAccessible(writeMethod);
		this.type = this.readMethod.getReturnType();
	}

	private static Method makeAccessible(final Method method)
	{
		if (method != null && !method.isAccessible())
		{
			logger.debug("Making method accessible: {}", method.toString());
			method.setAccessible(true);
		}
		return method;
	}

	public final Set<String> getCategoriesFromAnnotation()
	{
		final ObjectDiffProperty annotation = readMethod.getAnnotation(ObjectDiffProperty.class);
		if (annotation != null)
		{
			return new TreeSet<String>(asList(annotation.categories()));
		}
		return Collections.emptySet();
	}

	public boolean isExcludedByAnnotation()
	{
		final ObjectDiffProperty annotation = readMethod.getAnnotation(ObjectDiffProperty.class);
		return annotation != null && annotation.excluded();
	}

	public String getPropertyName()
	{
		return this.propertyName;
	}

	/**
	 * Private function to allow looking for the field recursively up the superclasses.
	 *
	 * @param clazz
	 * @return
	 */
	private Set<Annotation> getFieldAnnotations(final Class<?> clazz)
	{
		try
		{
			return new LinkedHashSet<Annotation>(asList(clazz.getDeclaredField(propertyName).getAnnotations()));
		}
		catch (final NoSuchFieldException e)
		{
			if (clazz.getSuperclass() != null)
			{
				return getFieldAnnotations(clazz.getSuperclass());
			}
			else
			{
				logger.debug("Cannot find propertyName: {}, declaring class: {}", propertyName, clazz);
				return new LinkedHashSet<Annotation>(0);
			}
		}
	}

	/**
	 * @return The annotations of the field, or an empty set if there is no field with the name derived from the getter.
	 */
	public Set<Annotation> getFieldAnnotations()
	{
		return getFieldAnnotations(readMethod.getDeclaringClass());
	}

	/**
	 * @return The given annotation of the field, or null if not annotated or if there is no field with the name derived
	 * from the getter.
	 */
	public <T extends Annotation> T getFieldAnnotation(final Class<T> annotationClass)
	{
		final Set<? extends Annotation> annotations = getFieldAnnotations();
		assert (annotations != null) : "Something is wrong here. " +
				"The contract of getReadAnnotations() guarantees a non-null return value.";
		for (final Annotation annotation : annotations)
		{
			if (annotationClass.isAssignableFrom(annotation.annotationType()))
			{
				return annotationClass.cast(annotation);
			}
		}
		return null;
	}

	/**
	 * @return The annotations of the getter used to access this property.
	 */
	public Set<Annotation> getReadMethodAnnotations()
	{
		return new LinkedHashSet<Annotation>(asList(readMethod.getAnnotations()));
	}

	public <T extends Annotation> T getReadMethodAnnotation(final Class<T> annotationClass)
	{
		final Set<? extends Annotation> annotations = getReadMethodAnnotations();
		assert (annotations != null) : "Something is wrong here. " +
				"The contract of getReadAnnotations() guarantees a non-null return value.";
		for (final Annotation annotation : annotations)
		{
			if (annotationClass.isAssignableFrom(annotation.annotationType()))
			{
				return annotationClass.cast(annotation);
			}
		}
		return null;
	}

	public BeanPropertyElementSelector getElementSelector()
	{
		return new BeanPropertyElementSelector(this.propertyName);
	}

	public Object get(final Object target)
	{
		if (target == null)
		{
			return null;
		}
		try
		{
			return readMethod.invoke(target);
		}
		catch (final Exception cause)
		{
			throw new PropertyReadException(propertyName, target.getClass(), cause);
		}
	}

	public void set(final Object target, final Object value)
	{
		if (target == null)
		{
			logger.info("Couldn't set new value '{}' for property '{}' " +
					"because the target object is null", value, propertyName);
		}
		else if (writeMethod == null)
		{
			logger.debug("No setter found for property '{}'", propertyName);
			tryToReplaceContentOfCollectionTypes(target, value);
		}
		else
		{
			invokeWriteMethod(target, value);
		}
	}

	public void unset(final Object target)
	{
		set(target, null);
	}

	@SuppressWarnings("unchecked")
	private void tryToReplaceContentOfCollectionTypes(final Object target, final Object value)
	{
		if (Collection.class.isAssignableFrom(readMethod.getReturnType()))
		{
			if (tryToReplaceCollectionContent((Collection<Object>) get(target), (Collection<Object>) value))
			{
				return;
			}
		}
		if (Map.class.isAssignableFrom(readMethod.getReturnType()))
		{
			if (tryToReplaceMapContent((Map<Object, Object>) get(target), (Map<Object, Object>) value))
			{
				return;
			}
		}
		logger.info("Couldn't set new value '{}' for property '{}'", value, propertyName);
	}

	private void invokeWriteMethod(final Object target, final Object value)
	{
		try
		{
			writeMethod.invoke(target, value);
		}
		catch (final Exception cause)
		{
			throw new PropertyWriteException(propertyName, target.getClass(), value, cause);
		}
	}

	private static boolean tryToReplaceCollectionContent(final Collection<Object> target,
														 final Collection<Object> value)
	{
		if (target == null)
		{
			return false;
		}
		try
		{
			target.clear();
			target.addAll(value);
			return true;
		}
		catch (final Exception unmodifiable)
		{
			logger.debug("Failed to replace content of existing Collection", unmodifiable);
			return false;
		}
	}

	private static boolean tryToReplaceMapContent(final Map<Object, Object> target,
												  final Map<Object, Object> value)
	{
		if (target == null)
		{
			return false;
		}
		try
		{
			target.clear();
			target.putAll(value);
			return true;
		}
		catch (final Exception unmodifiable)
		{
			logger.debug("Failed to replace content of existing Map", unmodifiable);
			return false;
		}
	}

	public Class<?> getType()
	{
		return this.type;
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder("PropertyAccessor{");
		sb.append("propertyName='").append(propertyName).append('\'');
		sb.append(", type=").append(type.getCanonicalName());
		sb.append(", source=").append(readMethod.getDeclaringClass().getCanonicalName());
		sb.append(", hasWriteMethod=").append(writeMethod != null);
		sb.append('}');
		return sb.toString();
	}
}
