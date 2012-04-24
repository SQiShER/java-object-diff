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

package de.danielbechler.diff.integration;

import de.danielbechler.diff.annotation.*;

/** @author Daniel Bechler */
@ObjectDiffEqualsOnlyType
public final class PhoneNumber
{
	private final String countryCode;
	private final String areaCode;
	private final String localNumber;

	public PhoneNumber(final String countryCode, final String areaCode, final String localNumber)
	{
		this.countryCode = countryCode;
		this.areaCode = areaCode;
		this.localNumber = localNumber;
	}

	public String getCountryCode()
	{
		return countryCode;
	}

	public String getAreaCode()
	{
		return areaCode;
	}

	public String getLocalNumber()
	{
		return localNumber;
	}

	@Override
	public boolean equals(final Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		final PhoneNumber that = (PhoneNumber) o;

		if (areaCode != null ? !areaCode.equals(that.areaCode) : that.areaCode != null)
		{
			return false;
		}
		if (countryCode != null ? !countryCode.equals(that.countryCode) : that.countryCode != null)
		{
			return false;
		}
		if (localNumber != null ? !localNumber.equals(that.localNumber) : that.localNumber != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = countryCode != null ? countryCode.hashCode() : 0;
		result = 31 * result + (areaCode != null ? areaCode.hashCode() : 0);
		result = 31 * result + (localNumber != null ? localNumber.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return countryCode + " (" + areaCode + ") " + localNumber;
	}
}
