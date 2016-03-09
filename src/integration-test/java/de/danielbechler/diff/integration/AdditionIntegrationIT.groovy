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

package de.danielbechler.diff.integration

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.example.phonebook.Contact
import de.danielbechler.diff.example.phonebook.PhoneBook
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
public class AdditionIntegrationIT extends Specification {

	def 'detects change from null to object referenct as addition'() {
		given:
		  def base = new ObjectWithString()
		  def working = new ObjectWithString("foo")
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		then:
		  node.getChild('value').state == DiffNode.State.ADDED
	}

	def 'detects change for duplicates in list'() {
		def node
		def joe = new Contact("Joe", "Smith")
		def objectDiffer = ObjectDifferBuilder.buildDefault()

		given:
		  def phoneBookServer = new PhoneBook("devs")
		  phoneBookServer.addContact(joe)
		and:
		  def phoneBookMobile = new PhoneBook("devs")
		  phoneBookMobile.addContact(joe)

		when:
		  node = objectDiffer.compare(phoneBookMobile, phoneBookServer)
		then:
		  node.state == DiffNode.State.UNTOUCHED

		when:
		  phoneBookMobile.addContact(joe)
		  node = objectDiffer.compare(phoneBookMobile, phoneBookServer)
		then:
		  phoneBookMobile.contacts.size() == 2
		and:
		  node.state == DiffNode.State.UNTOUCHED
//		  // Eventually it should be able to detect the change as addition...
//		  node.state == DiffNode.State.ADDED
	}
}
