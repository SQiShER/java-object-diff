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
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import org.testng.annotations.*;

import static de.danielbechler.diff.node.NodeAssertions.*;
import static org.testng.AssertJUnit.assertEquals;

/** @author Daniel Bechler */
public class AdditionIntegrationTest
{
	@Test
	public void testDetectsChangeFromNullToObjectReferenctAsAddition() throws Exception
	{
		final ObjectWithString base = new ObjectWithString();
		final ObjectWithString working = new ObjectWithString("foo");

		final Node node = ObjectDifferFactory.getInstance().compare(working, base);

		assertThat(node).child("value").hasState(Node.State.ADDED);
	}

    @Test
    public void testDetectsChangeForDuplicatesInList() throws Exception
    {
        final Contact joe = new Contact("Joe", "Smith");
        final PhoneBook phoneBookServer = new PhoneBook("devs");
        phoneBookServer.addContact(joe);
        final PhoneBook phoneBookMobile = new PhoneBook("devs");
        phoneBookMobile.addContact(joe);

        assertEquals(Node.State.UNTOUCHED, ObjectDifferFactory.getInstance().compare(phoneBookMobile, phoneBookServer).getState());
        phoneBookMobile.addContact(joe);
        assertEquals(2, phoneBookMobile.getContacts().size());
        //Should be ADDED!
        //assertEquals(Node.State.ADDED, ObjectDifferFactory.getInstance().compare(phoneBookMobile, phoneBookServer).getState());
        assertEquals(Node.State.UNTOUCHED, ObjectDifferFactory.getInstance().compare(phoneBookMobile, phoneBookServer).getState());
    }
}
