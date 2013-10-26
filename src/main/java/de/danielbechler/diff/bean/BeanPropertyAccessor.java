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
import de.danielbechler.util.*;
import org.slf4j.*;

import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
public class BeanPropertyAccessor implements PropertyAwareAccessor, Accessor
{
	private static final Logger logger = LoggerFactory.getLogger(BeanPropertyAccessor.class);

	private final String propertyName;
	private final Class<?> type;
	private final Method readMethod;
	private final Method writeMethod;
	private Set<String> categories = new TreeSet<String>();
	private boolean excluded;
	private ComparisonStrategy comparisonStrategy;

	public BeanPropertyAccessor(final String propertyName, final Method readMethod, final Method writeMethod)
	{
		Assert.notNull(propertyName, "propertyName");
		Assert.notNull(readMethod, "readMethod");
		this.propertyName = propertyName;
		this.readMethod = makeAccessible(readMethod);
		this.writeMethod = makeAccessible(writeMethod);
		this.type = this.readMethod.getReturnType();
	}

	public final Set<String> getCategories()
	{
		return categories;
	}

	public final void setCategories(final Set<String> categories)
	{
		this.categories = categories;
	}

	public boolean isExcluded()
	{
		return excluded;
	}

	public void setExcluded(final boolean excluded)
	{
		this.excluded = excluded;
	}

	public ComparisonStrategy getComparisonStrategy()
	{
		return comparisonStrategy;
	}

	public void setComparisonStrategy(final ComparisonStrategy comparisonStrategy)
	{
		this.comparisonStrategy = comparisonStrategy;
	}

	private static Method makeAccessible(final Method method)
	{
		if (method != null)
		{
			method.setAccessible(true);
		}
		return method;
	}

	public void set(final Object target, final Object value)
	{
		if (target == null)
		{
			logger.debug("The target object is null");
			logFailedSet(value);
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

	private void logFailedSet(final Object value)
	{
		logger.info("Couldn't set new value '{}' for property '{}'", value, propertyName);
	}

	private void invokeWriteMethod(final Object target, final Object value)
	{
		try
		{
			writeMethod.invoke(target, value);
		}
		catch (Exception e)
		{
			final BeanPropertyWriteException ex = new BeanPropertyWriteException(e, value);
			ex.setPropertyName(propertyName);
			ex.setTargetType(getType());
			throw ex;
		}
	}

	private void tryToReplaceContentOfCollectionTypes(final Object target, final Object value)
	{
		if (Collection.class.isAssignableFrom(readMethod.getReturnType()))
		{
			//noinspection unchecked
			tryToReplaceCollectionContent((Collection<Object>) get(target), (Collection<Object>) value);
			return;
		}

		if (Map.class.isAssignableFrom(readMethod.getReturnType()))
		{
			//noinspection unchecked
			tryToReplaceMapContent((Map<Object, Object>) get(target), (Map<Object, Object>) value);
			return;
		}

		logFailedSet(value);
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
		catch (Exception unmodifiable)
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
		catch (Exception unmodifiable)
		{
			logger.debug("Failed to replace content of existing Map", unmodifiable);
			return false;
		}
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
		catch (Exception e)
		{
			final BeanPropertyReadException ex = new BeanPropertyReadException(e);
			ex.setPropertyName(propertyName);
			ex.setTargetType(target.getClass());
			throw ex;
		}
	}

	public void unset(final Object target)
	{
		set(target, null);
	}

	public Class<?> getType()
	{
		return this.type;
	}

	public String getPropertyName()
	{
		return this.propertyName;
	}

	public Element getPathElement()
	{
		return new NamedPropertyElement(this.propertyName);
	}

	/** @return The annotations of the getter used to access this property. */
	public Set<Annotation> getReadMethodAnnotations()
	{
		return new LinkedHashSet<Annotation>(Arrays.asList(readMethod.getAnnotations()));
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

	@Override
	public String toString()
	{
		return "property '" + propertyName + "'";
	}
}
