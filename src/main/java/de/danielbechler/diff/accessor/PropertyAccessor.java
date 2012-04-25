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

package de.danielbechler.diff.accessor;

import de.danielbechler.diff.accessor.exception.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;
import org.slf4j.*;

import java.lang.reflect.*;
import java.util.*;

/** @author Daniel Bechler */
public final class PropertyAccessor extends AbstractAccessor implements TypeAwareAccessor
{
	private static final Logger logger = LoggerFactory.getLogger(PropertyAccessor.class);

	private final String propertyName;
	private final Class<?> propertyType;
	private final Method readMethod;
	private final Method writeMethod;

	public PropertyAccessor(final String propertyName, final Method readMethod, final Method writeMethod)
	{
		Assert.notNull(propertyName, "propertyName");
		Assert.notNull(readMethod, "readMethod");
		this.propertyName = propertyName;
		this.readMethod = makeAccessible(readMethod);
		this.writeMethod = makeAccessible(writeMethod);
		this.propertyType = this.readMethod.getReturnType();
	}

	private static Method makeAccessible(final Method method)
	{
		if (method != null)
		{
			// I'm not sure, why I suddenly need this, but since I integrated the standard Java Introspector, I do.
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
			logFailedSet(value);

			final PropertyWriteException ex = new PropertyWriteException(e);
			ex.setPropertyName(propertyName);
			ex.setTargetType(getPropertyType());
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

	private static boolean tryToReplaceCollectionContent(final Collection<Object> target, final Collection<Object> value)
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

	private static boolean tryToReplaceMapContent(final Map<Object, Object> target, final Map<Object, Object> value)
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
			final PropertyReadException ex = new PropertyReadException(e);
			ex.setPropertyName(propertyName);
			ex.setTargetType(target.getClass());
			throw ex;
		}
	}

	public void unset(final Object target)
	{
		set(target, null);
	}

	public Class<?> getPropertyType()
	{
		return this.propertyType;
	}

	public String getPropertyName()
	{
		return this.propertyName;
	}

	public PropertyPath.Element getPathElement()
	{
		return new NamedPropertyElement(this.propertyName);
	}
}
