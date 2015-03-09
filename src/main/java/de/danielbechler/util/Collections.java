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

import java.util.*;

/** @author Daniel Bechler */
public class Collections
{
	private Collections()
	{
	}

//	public static <T> Set<T> setOf(final T... c)
//	{
//		return setOf(Arrays.asList(c));
//	}

	public static <T> Set<T> setOf(final Collection<T> c)
	{
		return new LinkedHashSet<T>(c);
	}

	public static boolean isEmpty(final Collection<?> c)
	{
		return c == null || c.isEmpty();
	}

	public static <T> boolean containsAny(final Iterable<T> haystack, final Iterable<T> needles)
	{
		if (haystack == null || needles == null)
		{
			return false;
		}
		for (final T straw : haystack)
		{
			for (final T needle : needles)
			{
				if (straw.equals(needle))
				{
					return true;
				}
			}
		}
		return false;
	}

//	public static <T> T get(final Iterable<T> haystack, final T needle)
//	{
//		for (final T t : haystack)
//		{
//			if (t.equals(needle))
//			{
//				return t;
//			}
//		}
//		return null;
//	}
//
//	public static <T> int indexOf(final Iterable<? extends T> haystack, final T needle)
//	{
//		int index = 0;
//		for (final T item : haystack)
//		{
//			if (item.equals(needle))
//			{
//				return index;
//			}
//			index++;
//		}
//		return -1;
//	}

	public static <T> Collection<? extends T> filteredCopyOf(final Collection<? extends T> source,
															 final Collection<? extends T> filter)
	{
		final Collection<T> copy;
		if (source != null)
		{
			copy = new LinkedList<T>(source);
		}
		else
		{
			copy = new LinkedList<T>();
		}
		if (filter != null)
		{
			copy.removeAll(new ArrayList<T>(filter));
		}
		return copy;
	}

//	public static <T> Collection<? extends T> maskedCopyOf(final Collection<? extends T> source,
//														   final Collection<? extends T> mask)
//	{
//		final Collection<T> copy = new LinkedList<T>(source);
//		copy.retainAll(new ArrayList<T>(mask));
//		return copy;
//	}

	public static <T> T firstElementOf(final Collection<? extends T> items)
	{
		if (items != null && !items.isEmpty())
		{
			return items.iterator().next();
		}
		return null;
	}

//	public static <T> T lastElementOf(final List<? extends T> items)
//	{
//		if (items != null && !items.isEmpty())
//		{
//			return items.get(items.size() - 1);
//		}
//		return null;
//	}
//
//	public static <T> T lastElementOf(final Collection<? extends T> items)
//	{
//		if (items != null && !items.isEmpty())
//		{
//			final Iterator<? extends T> iterator = items.iterator();
//			while (iterator.hasNext())
//			{
//				final T t = iterator.next();
//				if (!iterator.hasNext())
//				{
//					return t;
//				}
//			}
//		}
//		return null;
//	}
}
