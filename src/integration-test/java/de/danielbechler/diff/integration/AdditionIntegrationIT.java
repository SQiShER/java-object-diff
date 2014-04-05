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

package de.danielbechler.diff.integration;

import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.example.phonebook.Contact;
import de.danielbechler.diff.example.phonebook.PhoneBook;
import de.danielbechler.diff.mock.ObjectWithString;
import org.testng.annotations.Test;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;

/**
 * @author Daniel Bechler
 */
public class AdditionIntegrationIT
{
	@Test
	public void detects_change_from_null_to_object_referenct_as_addition() throws Exception
	{
		final ObjectWithString base = new ObjectWithString();
		final ObjectWithString working = new ObjectWithString("foo");

		final DiffNode node = ObjectDifferBuilder.buildDefault().compare(working, base);

		assertThat(node).child("value").hasState(DiffNode.State.ADDED);
	}

	@Test
	public void testDetectsChangeForDuplicatesInList() throws Exception
	{
		final Contact joe = new Contact("Joe", "Smith");
		final PhoneBook phoneBookServer = new PhoneBook("devs");
		phoneBookServer.addContact(joe);
		final PhoneBook phoneBookMobile = new PhoneBook("devs");
		phoneBookMobile.addContact(joe);

		assertEquals(DiffNode.State.UNTOUCHED, ObjectDifferBuilder.buildDefault().compare(phoneBookMobile, phoneBookServer).getState());
		phoneBookMobile.addContact(joe);
		assertEquals(2, phoneBookMobile.getContacts().size());
		//Should be ADDED!
		//assertEquals(DiffNode.State.ADDED, ObjectDifferFactory.getInstance().compare(phoneBookMobile, phoneBookServer).getState());
		assertEquals(DiffNode.State.UNTOUCHED, ObjectDifferBuilder.buildDefault().compare(phoneBookMobile, phoneBookServer).getState());
	}
}
