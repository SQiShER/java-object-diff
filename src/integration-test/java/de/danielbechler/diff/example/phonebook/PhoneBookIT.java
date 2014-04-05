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

import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.collection.CollectionItemElementSelector;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author Daniel Bechler
 */
public class PhoneBookIT
{
	@Test
	public void testContactMiddleNameChanges()
	{
		final PhoneBook phoneBook = new PhoneBook("Breaking Bad");

		final Contact walterWhite = new Contact("Walter", "White");
		walterWhite.setPhoneNumber("Home", new PhoneNumber("1", "505", "316-7871"));
		walterWhite.setPhoneNumber("Work", new PhoneNumber("1", "505", "456-3788"));
		phoneBook.addContact(walterWhite);

		final Contact jessePinkman = new Contact("Jesse", "Pinkman");
		jessePinkman.setPhoneNumber("Home", new PhoneNumber("1", "505", "234-4628"));
		phoneBook.addContact(jessePinkman);

		final PhoneBook modifiedPhoneBook = PhoneBook.from(phoneBook);
		modifiedPhoneBook.getContact("Jesse", "Pinkman").setMiddleName("Bruce");
		modifiedPhoneBook.getContact("Walter", "White").setMiddleName("Hartwell");

		final ObjectDiffer objectDiffer = ObjectDifferBuilder.buildDefault();
		final DiffNode node = objectDiffer.compare(modifiedPhoneBook, phoneBook);

		assertThat(node.hasChanges(), is(true));
		assertThat(node.hasChildren(), is(true));
		assertThat(node.childCount(), is(1));

		final DiffNode contactsNode = node.getChild("contacts");
		assertThat(contactsNode, IsNull.notNullValue());
		assertThat(contactsNode.hasChanges(), is(true));

		final DiffNode pinkmanNode = contactsNode.getChild(new CollectionItemElementSelector(jessePinkman));
		assertThat(pinkmanNode.hasChanges(), is(true));

		final DiffNode middleNameNode = pinkmanNode.getChild("middleName");
		assertThat(middleNameNode.hasChanges(), is(true));
		assertThat(middleNameNode.canonicalGet(phoneBook), IsNull.nullValue());
		assertThat((String) middleNameNode.canonicalGet(modifiedPhoneBook), IsEqual.equalTo("Bruce"));

		final DiffNode whiteNode = contactsNode.getChild(new CollectionItemElementSelector(walterWhite));
		assertThat(whiteNode.hasChanges(), is(true));

		final DiffNode whiteMiddleNameNode = whiteNode.getChild("middleName");
		assertThat(whiteMiddleNameNode.hasChanges(), is(true));
		assertThat(whiteMiddleNameNode.canonicalGet(phoneBook), IsNull.nullValue());
		assertThat((String) whiteMiddleNameNode.canonicalGet(modifiedPhoneBook), IsEqual.equalTo("Hartwell"));
	}
}
