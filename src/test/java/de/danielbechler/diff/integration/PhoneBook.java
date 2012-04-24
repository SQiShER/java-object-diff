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

import de.danielbechler.util.*;

import java.util.*;

/** @author Daniel Bechler */
public class PhoneBook
{
	public static PhoneBook from(final PhoneBook phoneBook)
	{
		final PhoneBook copy = new PhoneBook(phoneBook.name);
		for (final Contact contact : phoneBook.contacts)
		{
			copy.addContact(Contact.from(contact));
		}
		return copy;
	}

	private String name;
	private List<Contact> contacts = new LinkedList<Contact>();

	public PhoneBook(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public List<Contact> getContacts()
	{
		return contacts;
	}

	public void setContacts(final List<Contact> contacts)
	{
		this.contacts = contacts;
	}

	public Contact getContact(final String firstName, final String lastName)
	{
		for (final Contact contact : contacts)
		{
			if (contact.getFirstName().equalsIgnoreCase(firstName) && contact.getLastName().equalsIgnoreCase(lastName))
			{
				return contact;
			}
		}
		return null;
	}

	public void addContact(final Contact contact)
	{
		this.contacts.add(contact);
	}

	@Override
	public String toString()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append(name).append(" Phone Book").append('\n');
		sb.append("-------------").append('\n');
		for (final Contact contact : contacts)
		{
			final String name = Strings.join(" ",
											 contact.getFirstName(),
											 contact.getMiddleName(),
											 contact.getLastName());
			sb.append(name).append(":\n");
			for (final Map.Entry<String, PhoneNumber> entry : contact.getPhoneNumbers().entrySet())
			{
				sb.append("  ")
						.append(entry.getKey())
						.append(": ")
						.append(entry.getValue())
						.append('\n');
			}
		}
		return sb.toString();
	}
}
