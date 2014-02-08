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

import de.danielbechler.diff.*;
import de.danielbechler.diff.example.phonebook.*;
import de.danielbechler.diff.mock.*;
import org.testng.annotations.*;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;
import static org.testng.AssertJUnit.assertEquals;

/** @author Daniel Bechler */
public class AdditionIntegrationITCase
{
	@Test
	public void detects_change_from_null_to_object_referenct_as_addition() throws Exception
	{
		final ObjectWithString base = new ObjectWithString();
		final ObjectWithString working = new ObjectWithString("foo");

		final DiffNode node = ObjectDifferBuilder.buildDefaultObjectDiffer().compare(working, base);

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

		assertEquals(DiffNode.State.UNTOUCHED, ObjectDifferBuilder.buildDefaultObjectDiffer().compare(phoneBookMobile, phoneBookServer).getState());
		phoneBookMobile.addContact(joe);
		assertEquals(2, phoneBookMobile.getContacts().size());
		//Should be ADDED!
		//assertEquals(DiffNode.State.ADDED, ObjectDifferFactory.getInstance().compare(phoneBookMobile, phoneBookServer).getState());
		assertEquals(DiffNode.State.UNTOUCHED, ObjectDifferBuilder.buildDefaultObjectDiffer().compare(phoneBookMobile, phoneBookServer).getState());
	}
}
