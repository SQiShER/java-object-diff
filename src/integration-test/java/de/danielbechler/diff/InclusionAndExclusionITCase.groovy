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

package de.danielbechler.diff

import de.danielbechler.diff.collection.CollectionItemElementSelector
import de.danielbechler.diff.visitor.PrintingVisitor
import spock.lang.Specification

import static de.danielbechler.diff.PhoneBookSetup.Contact

/**
 * Created by dbechler.
 */
class InclusionAndExclusionITCase extends Specification {

	def builder = ObjectDifferBuilder.startBuilding()
	def configurable = builder.configure()
	def working = PhoneBookSetup.getWorking()
	def base = PhoneBookSetup.getBase()

	def "Sanity check"() {
		when:
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)

		then:
		  node.getChild('name').changed

		and: "only two contacts should have changed"
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 2

		and: "only Georges name should have changed"
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'george'))).childCount() == 1
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'george'))).getChild('name').changed

		and: "only Kramers number should have changed"
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'kramer'))).childCount() == 1
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'kramer'))).getChild('number').changed
	}

	def "Property with specific name excluded via configuration"() {
		given:
		  configurable.inclusion().toExclude().propertyNames("name")

		when:
		  def node = builder.build().compare(working, base)

		then: "Georges name change should be ignored"
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'george'))) == null

		and: "The name change of the phone book should be ignored too"
		  node.getChild('name') == null
	}

	def "Category excluded via configuration"() {
		given:
		  configurable.inclusion().toExclude().categories('private')

		when:
		  def node = builder.build().compare(working, base)

		then: "Kramers changed number should be ignored"
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'kramer'))) == null
	}

	def "Type excluded via configuration"() {
		given:
		  configurable.inclusion().toExclude().types(Contact)

		when:
		  def node = builder.build().compare(working, base)

		then: "all contact changes should be ignored"
		  node.getChild('contacts') == null
	}

	def "Node at specific NodePath excluded via configuration"() {
		given:
		  configurable.inclusion().toExclude().node(NodePath
				  .startBuilding()
				  .propertyName('contacts')
				  .collectionItem(new Contact(id: 'george'))
				  .build())

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').getChild(new CollectionItemElementSelector(new Contact(id: 'george'))) == null
	}

	def "Property excluded via @ObjectDiffProperty annotation"() {
		given:
		  configurable.inclusion().toExclude().node(NodePath.with('name'))

		when:
		  def node = builder.build().compare(working, base)

		then: "the name change of the phone book should be ignored"
		  node.getChild('revision') == null
	}

	def "including an element via property name"() {
		given:
		  configurable.inclusion().toInclude().propertyNames('name')

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild("name").changed
		  node.getChild("contacts") == null
	}

	def "including an element via property name includes all its children"() {
		given:
		  configurable.inclusion().toInclude().propertyNames('contacts')

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild("contacts").childCount() == 2
	}

	def "including an element via path includes all its children"() {

	}

	def "including an element via category"() {
		def includedCategory = "representation"

		given: "the name property of the phonebook is part of our included category"
		  configurable.categories().ofNode(NodePath.with("name")).toBe(includedCategory)

		and: "the category is included"
		  configurable.inclusion().toInclude().categories(includedCategory)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('name').changed
	}

	def "including an element implicitly includes its children"() {
		given:
		  configurable.inclusion().toInclude().node(NodePath.with('contacts'))

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 2
	}

	def "include all but some specific elements"() {
		given:
		  configurable.inclusion().toInclude().node(NodePath.startBuilding().propertyName('contacts').any().build())
		  configurable.inclusion().toExclude().node(NodePath.startBuilding().propertyName('contacts').collectionItem(new Contact(id: 'kramer')).build())

		when:
		  def node = builder.build().compare(working, base)

		and:
		  node.visit(new PrintingVisitor(working, base))

		then:
		  node.getChild('contacts').changed
		  node.getChild('contacts').childCount() == 1
	}

	def "including an element via category only includes properties if any of their parent elements is also somehow included"() {
		def includedCategory = "representation"
		def nodePathToKramer = NodePath.startBuilding()
				.propertyName("contacts")
				.collectionItem(new Contact(id: "kramer"))
				.build()

		given:
		  configurable.categories().ofNode(NodePath.with("name")).toBe(includedCategory)
		  configurable.categories().ofNode(nodePathToKramer).toBe(includedCategory)

		and: "the category is included"
		  configurable.inclusion().toInclude().categories(includedCategory)

		when:
		  def node = builder.build().compare(working, base)

		then:
		  node.getChild('name').changed
	}

	def "including an element only works if its parent element is also included"() {
	}

	def "children of included elements can be excluded"() {
	}

	def "elements can be excluded via wildcard"() {
	}

	def "elements can be excluded via exclude-all-but(x, y, ...) rule"() {
	}
}
