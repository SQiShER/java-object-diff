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

package de.danielbechler.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static java.util.Arrays.asList;

public final class Classes
{
	private static final Logger logger = LoggerFactory.getLogger(Classes.class);
	private static final Set<Class<?>> PRIMITIVE_WRAPPER_TYPES = getPrimitiveWrapperTypes();
	private static final Collection<Class<?>> PRIMITIVE_NUMERIC_TYPES = getPrimitiveNumericTypes();
	//	private static final List<?> COMPARABLE_TYPES = Arrays.asList(
	//			// The Extendables
	//			// since Java 1.8
	//			Duration.class,
	//			Instant.class,
	//			LocalDate.class,
	//			LocalDateTime.class,
	//			LocalTime.class,
	//			MonthDay.class,
	//			OffsetDateTime.class,
	//			OffsetTime.class,
	//			Year.class,
	//			YearMonth.class,
	//			ZonedDateTime.class,
	//			ZoneOffset.class
	//	);
	@SuppressWarnings("unchecked")
	private static final Collection<Class<?>> EXTENDABLE_SIMPLE_TYPES = asList(
			BigDecimal.class,
			BigInteger.class,
			CharSequence.class, // String, StringBuilder, etc.
			Calendar.class, // GregorianCalendar, etc.
			Date.class, // java.sql.Date, java.util.Date, java.util.Time, etc.
			Enum.class // enums... duh

	);
	@SuppressWarnings("unchecked")
	private static final List<Class<? extends Serializable>> FINAL_SIMPLE_TYPES = asList(
			Class.class,
			URI.class,
			URL.class,
			Locale.class,
			UUID.class
	);

	private Classes()
	{
	}

	private static Set<Class<?>> getPrimitiveWrapperTypes()
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
//
// The following are not numeric types and tests fail when they are commented in. Feels weird...
//
//		numericTypes.add(boolean.class);
//		numericTypes.add(void.class);
//
		return numericTypes;
	}

	public static boolean isComparableType(final Class<?> clazz)
	{
		return Comparable.class.isAssignableFrom(clazz);
	}

	public static boolean isSimpleType(final Class<?> clazz)
	{
		if (clazz == null)
		{
			return false;
		}
		else if (isPrimitiveType(clazz))
		{
			return true;
		}
		else if (isPrimitiveWrapperType(clazz))
		{
			return true;
		}
		for (final Class<?> type : FINAL_SIMPLE_TYPES)
		{
			if (type.equals(clazz))
			{
				return true;
			}
		}
		for (final Class<?> type : EXTENDABLE_SIMPLE_TYPES)
		{
			if (type.isAssignableFrom(clazz))
			{
				return true;
			}
		}
		return false;
	}

	public static boolean isPrimitiveType(final Class<?> clazz)
	{
		return clazz != null && clazz.isPrimitive();
	}

	public static boolean isPrimitiveWrapperType(final Class<?> clazz)
	{
		return clazz != null && PRIMITIVE_WRAPPER_TYPES.contains(clazz);
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
		catch (final NoSuchMethodException e)
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
		catch (final Exception e)
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

	/**
	 * This method will not extract the interfaces of the given types, since there is no way to determine, which
	 * interface is the most specific one (due to their compositional nature). However, if one or more interfaces
	 * are given as type, they will be considered as most specific shared type.
	 *
	 * @return The most specific class (or interface) shared by all given types.
	 */
	public static Class<?> mostSpecificSharedType(final Collection<Class<?>> types)
	{
		final Collection<Class<?>> potentiallySharedTypes = superclassesOf(types);
		potentiallySharedTypes.addAll(types);

		final Collection<Class<?>> sharedTypes = new TreeSet<Class<?>>(new ClassComparator());
		for (final Class<?> potentiallySharedType : potentiallySharedTypes)
		{
			int matches = 0;
			for (final Class<?> type : types)
			{
				if (potentiallySharedType.isAssignableFrom(type))
				{
					matches++;
				}
			}
			if (matches == types.size())
			{
				sharedTypes.add(potentiallySharedType);
			}
		}
		if (sharedTypes.isEmpty())
		{
			return null;
		}
		return sharedTypes.iterator().next();
	}

	private static Collection<Class<?>> superclassesOf(final Iterable<Class<?>> types)
	{
		final Collection<Class<?>> superclasses = new HashSet<Class<?>>();
		for (final Class<?> type : types)
		{
			Class<?> superclass = type.getSuperclass();
			while (superclass != null && superclass != Object.class)
			{
				superclasses.add(superclass);
				superclass = superclass.getSuperclass();
			}
		}
		return superclasses;
	}

	private static class ClassComparator implements Comparator<Class<?>>, Serializable
	{
		private static final long serialVersionUID = 56568941407903459L;

		public int compare(final Class<?> o1, final Class<?> o2)
		{
			if (o1.isAssignableFrom(o2))
			{
				return 1;
			}
			else if (o2.isAssignableFrom(o1))
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
	}
}
