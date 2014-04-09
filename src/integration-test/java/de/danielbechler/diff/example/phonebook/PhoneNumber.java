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

package de.danielbechler.diff.example.phonebook;

import de.danielbechler.diff.introspection.ObjectDiffEqualsOnlyType;

/**
 * @author Daniel Bechler
 */
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
