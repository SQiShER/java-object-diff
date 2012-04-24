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

package de.danielbechler.util;

import org.slf4j.*;

import java.net.*;
import java.util.*;

/** @author Daniel Bechler */
public final class Classes
{
	private static final Logger logger = LoggerFactory.getLogger(Classes.class);

	private Classes()
	{
	}

	public static boolean isSimpleType(final Class<?> clazz)
	{
		return clazz.isPrimitive() ||
				clazz.equals(Boolean.class) || clazz.equals(boolean.class) ||
				clazz.equals(Byte.class) || clazz.equals(byte.class) ||
				clazz.equals(Character.class) || clazz.equals(char.class) ||
				clazz.equals(Double.class) || clazz.equals(double.class) ||
				clazz.equals(Float.class) || clazz.equals(float.class) ||
				clazz.equals(Integer.class) || clazz.equals(int.class) ||
				clazz.equals(Long.class) || clazz.equals(long.class) ||
				clazz.equals(Short.class) || clazz.equals(short.class) ||
				clazz.isEnum() ||
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
			logger.warn("Couldn't find default constructor of type {}. Assuming 'null' is default.", clazz.getName());
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
}
