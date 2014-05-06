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
import de.danielbechler.diff.selector.CollectionItemElementSelector
import spock.lang.Specification

import static PhoneBookSetup.Contact

/**
 * Created by dbechler.
 */
class InclusionIT extends Specification {

	static final GEORGE_SELECTOR = new CollectionItemElementSelector(new Contact(id: 'george'))
	static final KRAMER_SELECTOR = new CollectionItemElementSelector(new Contact(id: 'kramer'))

	def builder = ObjectDifferBuilder.startBuilding()
	def working = PhoneBookSetup.getWorking()
	def base = PhoneBookSetup.getBase()

	def "sanity check"() {
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)

		then:
		  node.getChild('name').changed

		and: "only two contacts should have changed"
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 2

		and: "only Georges name should have changed"
		  node.getChild('contacts').getChild(GEORGE_SELECTOR).childCount() == 1
		  node.getChild('contacts').getChild(GEORGE_SELECTOR).getChild('name').changed

		and: "only Kramers number should have changed"

		  node.getChild('contacts').getChild(KRAMER_SELECTOR).childCount() == 1
		  node.getChild('contacts').getChild(KRAMER_SELECTOR).getChild('number').changed
	}

	def "Property with specific name excluded via configuration"() {
		given:
		  builder.inclusion().exclude().propertyName("name")

		when:
		  def node = builder.build().compare(working, base)

		then: "Georges name change should be ignored"
		  node.getChild('contacts').getChild(GEORGE_SELECTOR) == null

		and: "The name change of the phone book should be ignored too"
		  node.getChild('name') == null
	}

	def "Category excluded via configuration"() {
		given:
		  builder.inclusion().exclude().category('private')

		when:
		  def node = builder.build().compare(working, base)

		then: "Kramers changed number should be ignored"
		  node.getChild('contacts').getChild(KRAMER_SELECTOR) == null
	}

	def "Type excluded via configuration"() {
		given:
		  builder.inclusion().exclude().type(Contact)

		when:
		  def node = builder.build().compare(working, base)

		then: "all contact changes should be ignored"
		  node.getChild('contacts') == null
	}

	def "Node at specific NodePath excluded via configuration"() {
		given:
		  builder.inclusion().exclude().node(NodePath
				  .startBuilding()
				  .propertyName('contacts')
				  .element(GEORGE_SELECTOR)
				  .build())

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').getChild(GEORGE_SELECTOR) == null
	}

	def "Property excluded via @ObjectDiffProperty annotation"() {
		given:
		  builder.inclusion().exclude().node(NodePath.with('name'))

		when:
		  def node = builder.build().compare(working, base)

		then: "the name change of the phone book should be ignored"
		  node.getChild('revision') == null
	}

	def "including an element via property name"() {
		given:
		  builder.inclusion().include().propertyName('name')

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild("name").changed
		  node.getChild("contacts") == null
	}

	def "including an element via property name includes all its children"() {
		given:
		  builder.inclusion().include().propertyName('contacts')

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild("contacts").childCount() == 2
	}

	def "including an element via path includes all its children"() {
		given:
		  builder.inclusion().include().node(NodePath.with('contacts'))

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild("contacts").childCount() == 2
	}

	def "including an element via category"() {
		def includedCategory = "representation"

		given: "the name property of the phonebook is part of our included category"
		  builder.categories().ofNode(NodePath.with("name")).toBe(includedCategory)

		and: "the category is included"
		  builder.inclusion().include().category(includedCategory)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('name').changed
	}

	def "including an element implicitly includes its children"() {
		given:
		  builder.inclusion().include().node(NodePath.with('contacts'))

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 2
	}

	def "include all but some specific elements"() {
		given:
		  builder.inclusion().include().node(NodePath.startBuilding().propertyName('contacts').build())
		  builder.inclusion().exclude().node(NodePath.startBuilding().propertyName('contacts').element(KRAMER_SELECTOR).build())

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 1
	}

	def "when an element is included by property name, all its children will be implicitly included"() {
		given:
		  builder.inclusion().include().node(NodePath.startBuilding().propertyName('contacts').build())

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.childCount() == 1
		  node.getChild('contacts').childCount() == 2
		  node.getChild('contacts').getChild(GEORGE_SELECTOR).changed
		  node.getChild('contacts').getChild(KRAMER_SELECTOR).changed
	}

	def "when an element is included by category, all its children will be implicitly included"() {
		given:
		  builder.categories().ofNode(NodePath.startBuilding().propertyName('contacts').build()).toBe('identifier')
		  builder.inclusion().include().category('identifier')

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.childCount() == 1
		  node.getChild('contacts').changed
	}

	def "included children of excluded nodes should also be excluded"() {
		given:
		  builder.inclusion().exclude().node(NodePath.startBuilding().propertyName('contacts').build())
		  builder.inclusion().include().node(NodePath.startBuilding().propertyName('contacts').element(GEORGE_SELECTOR).build())

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts') == null
	}

	def "including a category includes matching properties only if they can be reached due to other inclusion rules"() {
		def includedCategory = "representation"
		def nodePathToKramer = NodePath.startBuilding()
				.propertyName("contacts")
				.element(KRAMER_SELECTOR)
				.build()

		given:
		  builder.categories().ofNode(NodePath.with("name")).toBe(includedCategory)
		  builder.categories().ofNode(nodePathToKramer).toBe(includedCategory)

		and: "the category is included"
		  builder.inclusion().include().category(includedCategory)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('name').changed
	}
}
