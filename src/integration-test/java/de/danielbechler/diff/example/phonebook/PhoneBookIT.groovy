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

package de.danielbechler.diff.example.phonebook

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.selector.CollectionItemElementSelector
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
public class PhoneBookIT extends Specification {

	def 'contact middle name changes'() {
		given:
		  PhoneBook phoneBook = new PhoneBook("Breaking Bad")
		and:
		  Contact walterWhite = new Contact("Walter", "White")
		  walterWhite.setPhoneNumber("Home", new PhoneNumber("1", "505", "316-7871"))
		  walterWhite.setPhoneNumber("Work", new PhoneNumber("1", "505", "456-3788"))
		  phoneBook.addContact(walterWhite)
		and:
		  Contact jessePinkman = new Contact("Jesse", "Pinkman")
		  jessePinkman.setPhoneNumber("Home", new PhoneNumber("1", "505", "234-4628"))
		  phoneBook.addContact(jessePinkman)
		and:
		  PhoneBook modifiedPhoneBook = PhoneBook.from(phoneBook)
		  modifiedPhoneBook.getContact("Jesse", "Pinkman").middleName = "Bruce"
		  modifiedPhoneBook.getContact("Walter", "White").middleName = "Hartwell"
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(modifiedPhoneBook, phoneBook)
		then:
		  node.hasChanges()
		  node.hasChildren()
		  node.childCount() == 1
		and:
		  def contactsNode = node.getChild("contacts")
		  contactsNode.hasChanges()
		and:
		  def pinkmanNode = contactsNode.getChild(new CollectionItemElementSelector(jessePinkman))
		  pinkmanNode.hasChanges()
		and:
		  def middleNameNode = pinkmanNode.getChild("middleName")
		  middleNameNode.hasChanges()
		  middleNameNode.canonicalGet(phoneBook) == null
		  middleNameNode.canonicalGet(modifiedPhoneBook) == "Bruce"
		and:
		  def whiteNode = contactsNode.getChild(new CollectionItemElementSelector(walterWhite))
		  whiteNode.hasChanges()
		and:
		  def whiteMiddleNameNode = whiteNode.getChild("middleName")
		  whiteMiddleNameNode.hasChanges()
		  whiteMiddleNameNode.canonicalGet(phoneBook) == null
		  whiteMiddleNameNode.canonicalGet(modifiedPhoneBook) == "Hartwell"
	}
}
