/*
 * Copyright 2014 Daniel Bechler
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

package de.danielbechler.diff.inclusion

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

import static PhoneBookSetup.Contact

/**
 * Created by Daniel Bechler.
 */
class IncludingAnElementViaNodePathIT extends Specification {

	def builder = ObjectDifferBuilder.startBuilding()
	def configurable = builder.configure()
	def working = PhoneBookSetup.getWorking()
	def base = PhoneBookSetup.getBase()
	def pathToContacts = NodePath.startBuilding().propertyName('contacts').build()
	def pathToContactKramer = NodePath.startBuildingFrom(pathToContacts).collectionItem(new Contact(id: 'kramer')).build()
	def pathToContactGeorge = NodePath.startBuildingFrom(pathToContacts).collectionItem(new Contact(id: 'george')).build()

	def 'should include its children as well'() {
		def includedNode = NodePath.with('contacts')

		given:
		  configurable.inclusion().include().node(includedNode)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild(includedNode).childCount() == 2
	}

	def 'should include its parents but not their children'() {
		given:
		  configurable.inclusion().include().node(pathToContactKramer)

		when:
		  def node = builder.build().compare(working, base)

		then: "kramer should be included, because he has been explicitly included"
		  node.getChild(pathToContactKramer).changed

		and: "george should be excluded, since he has not been explicitly included"
		  node.getChild(pathToContactGeorge) == null
	}

	def 'ideas'() {
		configurable.inclusion().include().node(pathToContactKramer)
	}
}
