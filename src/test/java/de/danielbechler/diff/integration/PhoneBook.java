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
