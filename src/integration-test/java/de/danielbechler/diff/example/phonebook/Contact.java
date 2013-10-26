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

import de.danielbechler.diff.annotation.*;
import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public class Contact
{
	public static Contact from(final Contact contact)
	{
		final Contact copy = new Contact(contact.firstName, contact.lastName);
		copy.setMiddleName(contact.middleName);
		copy.setPhoneNumbers(new LinkedHashMap<String, PhoneNumber>(contact.phoneNumbers));
		return copy;
	}

	private final String firstName;
	private final String lastName;
	private String middleName;
	private Map<String, PhoneNumber> phoneNumbers = new LinkedHashMap<String, PhoneNumber>(5);

	public Contact(final String firstName, final String lastName)
	{
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@ObjectDiffProperty(categories = "name")
	public String getFirstName()
	{
		return firstName;
	}

	@ObjectDiffProperty(categories = "name")
	public String getLastName()
	{
		return lastName;
	}

	@ObjectDiffProperty(categories = "name")
	public String getMiddleName()
	{
		return middleName;
	}

	public void setMiddleName(final String middleName)
	{
		this.middleName = middleName;
	}

	public Map<String, PhoneNumber> getPhoneNumbers()
	{
		return phoneNumbers;
	}

	public void setPhoneNumbers(final Map<String, PhoneNumber> phoneNumbers)
	{
		this.phoneNumbers = phoneNumbers;
	}

	public void setPhoneNumber(final String kind, final PhoneNumber phoneNumber)
	{
		this.phoneNumbers.put(kind, phoneNumber);
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

		final Contact contact = (Contact) o;

		if (firstName != null ? !firstName.equals(contact.firstName) : contact.firstName != null)
		{
			return false;
		}
		if (lastName != null ? !lastName.equals(contact.lastName) : contact.lastName != null)
		{
			return false;
		}

		return true;
	}

	@Override
	public int hashCode()
	{
		int result = firstName != null ? firstName.hashCode() : 0;
		result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return new StringBuilder().append(Strings.join(" ", firstName, lastName)).toString();
	}
}
