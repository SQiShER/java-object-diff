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

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.net.*;
import java.util.*;

/** @author Daniel Bechler */
public final class Classes
{
	private static final Logger logger = LoggerFactory.getLogger(Classes.class);

	private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
	private static final Collection<Class<?>> PRIMITIVE_NUMERIC_TYPES = getPrimitiveNumericTypes();

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

	public static boolean isPrimitiveNumericType(final Class<?> clazz)
	{
//		numericTypes.add(Character.class);
//		numericTypes.add(Byte.class);
//		numericTypes.add(Short.class);
//		numericTypes.add(Integer.class);
//		numericTypes.add(Long.class);
//		numericTypes.add(Float.class);
//		numericTypes.add(Double.class);
		return PRIMITIVE_NUMERIC_TYPES.contains(clazz);
	}

	private static Collection<Class<?>> getPrimitiveNumericTypes()
	{
		final Collection<Class<?>> numericTypes = new HashSet<Class<?>>();
		numericTypes.add(char.class);
		numericTypes.add(byte.class);
		numericTypes.add(short.class);
		numericTypes.add(int.class);
		numericTypes.add(long.class);
		numericTypes.add(float.class);
		numericTypes.add(double.class);
		return numericTypes;
	}

	public static boolean isPrimitiveType(final Class<?> clazz)
	{
		return clazz != null && clazz.isPrimitive();
	}

	public static boolean isPrimitiveWrapperType(final Class<?> clazz)
	{
		return clazz != null && WRAPPER_TYPES.contains(clazz);
	}

	public static boolean isSimpleType(final Class<?> clazz)
	{
		return isPrimitiveType(clazz) ||
				isPrimitiveWrapperType(clazz) ||
				clazz.isEnum() ||
				CharSequence.class.isAssignableFrom(clazz) ||
				Number.class.isAssignableFrom(clazz) ||
				Date.class.isAssignableFrom(clazz) ||
				URI.class.equals(clazz) ||
				URL.class.equals(clazz) ||
				Locale.class.equals(clazz) ||
				Class.class.equals(clazz);
	}

	public static boolean isComparableType(final Class<?> clazz)
	{
		return BigDecimal.class.equals(clazz);
	}

	public static <T> T freshInstanceOf(final Class<T> clazz)
	{
		if (clazz == null)
		{
			return null;
		}
		final Constructor<T> constructor;
		try
		{
			constructor = clazz.getDeclaredConstructor();
		}
		catch (NoSuchMethodException e)
		{
			logger.debug("Missing default constructor for type {}. Assuming standard default values " +
					"for primitive properties.", clazz.getName());
			return null;
		}
		final boolean accessibility = constructor.isAccessible();
		try
		{
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			constructor.setAccessible(accessibility);
		}
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
