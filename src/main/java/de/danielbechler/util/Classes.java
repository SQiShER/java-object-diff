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

package de.danielbechler.util;

import org.slf4j.*;

import java.net.*;
import java.util.*;

/** @author Daniel Bechler */
public final class Classes
{
	private static final Logger logger = LoggerFactory.getLogger(Classes.class);

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

	private Classes()
	{
	}

	private static Set<Class<?>> getWrapperTypes()
	{
		final Set<Class<?>> wrapperTypes = new HashSet<Class<?>>();
		wrapperTypes.add(Boolean.class);
		wrapperTypes.add(Character.class);
		wrapperTypes.add(Byte.class);
		wrapperTypes.add(Short.class);
		wrapperTypes.add(Integer.class);
		wrapperTypes.add(Long.class);
		wrapperTypes.add(Float.class);
		wrapperTypes.add(Double.class);
		wrapperTypes.add(Void.class);
		return wrapperTypes;
	}

	public static boolean isWrapperType(final Class<?> clazz)
	{
		return WRAPPER_TYPES.contains(clazz);
	}

	public static boolean isSimpleType(final Class<?> clazz)
	{
		return clazz.isPrimitive() || isWrapperType(clazz) || clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				clazz.equals(URI.class) ||
				clazz.equals(URL.class) ||
				clazz.equals(Locale.class) ||
				clazz.equals(Class.class);
	}

	public static <T> T freshInstanceOf(final Class<T> clazz)
	{
		try
		{
			return clazz.getConstructor().newInstance();
		}
		catch (NoSuchMethodException e)
		{
			logger.debug("Couldn't find default constructor of type {}. Assuming 'null' is default.", clazz.getName());
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Set<Class<?>> typesOf(final Object... values)
	{
		final Set<Class<?>> types = new HashSet<Class<?>>(values.length);
		for (final Object value : values)
		{
			if (value != null)
			{
				types.add(value.getClass());
			}
		}
		return types;
	}

	public static boolean allAssignableFrom(final Class<?> sharedType,
											final Iterable<? extends Class<?>> types)
	{
		boolean matching = true;
		for (final Class<?> type : types)
		{
			if (!sharedType.isAssignableFrom(type))
			{
				matching = false;
			}
		}
		return matching;
	}

}
