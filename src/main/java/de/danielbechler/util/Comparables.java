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

/**
 * @author Daniel Bechler
 */
@SuppressWarnings("ClassUnconnectedToPackage")
public class Comparables
{
	private Comparables()
	{
	}

	public static <T extends Comparable<T>> boolean isEqualByComparison(final T a, final T b)
	{
		if (a == null && b == null)
		{
			return true;
		}
		else if (a != null && b != null)
		{
			// When testing the comparison of java.util.Date and java.sql.Timestamp I noticed,
			// that they were never considered equal, because java.util.Date was used as the
			// working object. This way only its compareTo method was used, but never the one
			// of java.sql.Date, although the subclass added some magic to make the objects
			// compatible. To remedy this, this method tests both objects against each other and
			// returns true when one of the comparisons returns true.
			return a.compareTo(b) == 0 || b.compareTo(a) == 0;
		}
		return false;
	}
}
