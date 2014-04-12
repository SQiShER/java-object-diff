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

package de.danielbechler.diff.example;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.selector.CollectionItemElementSelector;
import de.danielbechler.diff.visitors.NodeHierarchyVisitor;

import java.util.LinkedList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * @author Daniel Bechler
 */
class CanonicalAccessorExample
{
	private CanonicalAccessorExample()
	{
	}

	@SuppressWarnings("unchecked")
	public static void main(final String[] args)
	{
		final AddressBook workingAddressBook = new AddressBook();
		final Contact workingContact = new Contact("Walter White", "Heisenberg");
		workingAddressBook.setContacts(asList(workingContact));

		final AddressBook baseAddressBook = new AddressBook();
		final Contact baseContact = new Contact("Walter White");
		baseAddressBook.setContacts(asList(baseContact));

		final DiffNode rootNode = ObjectDifferBuilder.buildDefault()
				.compare(workingAddressBook, baseAddressBook);
		final DiffNode contactsNode = rootNode.getChild("contacts");
		final DiffNode contactNode = contactsNode.getChild(new CollectionItemElementSelector(baseContact));
		final DiffNode nicknameNode = contactNode.getChild("nickname");

		rootNode.visit(new NodeHierarchyVisitor());

		/*

		Output:

		/ ===> DefaultNode(state=CHANGED, type=de.danielbechler.diff.example.CanonicalAccessorExample.AddressBook, 1 child, accessed via root element)
		  /contacts ===> CollectionNode(state=CHANGED, type=java.util.List, 1 child, accessed via property 'contacts')
		    /contacts[Contact{name='Walter White'}] ===> DefaultNode(state=CHANGED, type=de.danielbechler.diff.example.CanonicalAccessorExample.Contact, 1 child, accessed via collection item [Contact{name='Walter White'}])
		      /contacts[Contact{name='Walter White'}]/nickname ===> DefaultNode(state=ADDED, type=java.lang.String, no children, accessed via property 'nickname')

		 */

		final AddressBook addressBook = (AddressBook) rootNode.get(workingAddressBook);
		final List<Contact> contacts = (List<Contact>) contactsNode.get(addressBook);
		final Contact contact = (Contact) contactNode.get(contacts);

		assert rootNode.get(workingAddressBook) == rootNode.canonicalGet(workingAddressBook);
		assert contactsNode.get(addressBook) == contactsNode.canonicalGet(workingAddressBook);
		assert contactNode.get(contacts) == contactNode.canonicalGet(workingAddressBook);
		assert nicknameNode.get(contact) == nicknameNode.canonicalGet(workingAddressBook);
	}

	public static class AddressBook
	{
		private List<Contact> contacts = new LinkedList<Contact>();

		public List<Contact> getContacts()
		{
			return contacts;
		}

		public void setContacts(final List<Contact> contacts)
		{
			this.contacts = contacts;
		}
	}

	public static class Contact
	{
		private final String name;
		private String nickname;

		public Contact(final String name)
		{
			this.name = name;
		}

		public Contact(final String name, final String nickname)
		{
			this.name = name;
			this.nickname = nickname;
		}

		public String getName()
		{
			return name;
		}

		public String getNickname()
		{
			return nickname;
		}

		public void setNickname(final String nickname)
		{
			this.nickname = nickname;
		}

		@Override
		public boolean equals(final Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (!(o instanceof Contact))
			{
				return false;
			}

			final Contact contact = (Contact) o;

			if (name != null ? !name.equals(contact.name) : contact.name != null)
			{
				return false;
			}

			return true;
		}

		@Override
		public int hashCode()
		{
			return name != null ? name.hashCode() : 0;
		}

		@Override
		public String toString()
		{
			final StringBuilder sb = new StringBuilder();
			sb.append("Contact");
			sb.append("{name='").append(name).append('\'');
			sb.append('}');
			return sb.toString();
		}
	}
}
