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
import spock.lang.Specification

import static de.danielbechler.diff.node.DiffNode.State.IGNORED

/**
 * Created by Daniel Bechler.
 */
class ParentTypeBasedPropertyInclusionViaConfigAT extends Specification {

	def objectDifferBuilderPreconfiguredToReturnIgnoredNodes

	def "setup"() {
		objectDifferBuilderPreconfiguredToReturnIgnoredNodes = ObjectDifferBuilder
				.startBuilding()
				.filtering().returnNodesWithState(IGNORED).and()
	}

	def "a property can be included via config API"() {
		given:
		  def base = new ObjectWithOneProperty(name: 'about-to-change')
		  def working = new ObjectWithOneProperty(name: 'changed')
		when:
		  def node = objectDifferBuilderPreconfiguredToReturnIgnoredNodes.inclusion()
				  .include().propertyNameOfType(ObjectWithOneProperty, 'name').and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('name').isChanged()
	}

	private class ObjectWithOneProperty {
		def name
	}

	def "a property can be excluded via config API"() {
		given:
		  def base = new ObjectWithOneProperty(name: 'about-to-change')
		  def working = new ObjectWithOneProperty(name: 'changed')
		when:
		  def node = objectDifferBuilderPreconfiguredToReturnIgnoredNodes.inclusion()
				  .exclude().propertyNameOfType(ObjectWithOneProperty, 'name').and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 1
		  node.getChild('name').isIgnored()
	}

	def "when a property is included all siblings that have not been explicitly included will be ignored"() {
		given:
		  def base = new ObjectWithTwoProperties(name: 'about-to-change', description: 'about-to-change')
		  def working = new ObjectWithTwoProperties(name: 'changed', description: 'changed')
		when:
		  def node = objectDifferBuilderPreconfiguredToReturnIgnoredNodes.inclusion()
				  .include().propertyNameOfType(ObjectWithTwoProperties, 'name').and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 2
		  node.getChild('name').isChanged()
		  node.getChild('description').isIgnored()
	}

	private class ObjectWithTwoProperties {
		def name
		def description
	}

	def "when a property is included it does not affect properties of other types"() {
		given:
		  def base = new ObjectWithTwoProperties(name: 'about-to-change', description: 'about-to-change')
		  def working = new ObjectWithTwoProperties(name: 'changed', description: 'changed')

		and: 'an ObjectDiffer with inclusions for a different object type'
		  def objectDiffer = objectDifferBuilderPreconfiguredToReturnIgnoredNodes.inclusion()
				  .include().propertyNameOfType(ObjectWithOneProperty, 'name').and()
				  .build()

		when:
		  def node = objectDiffer.compare(working, base)

		then:
		  node.childCount() == 2
		  node.getChild('name').isChanged()
		  node.getChild('description').isChanged()
	}

	def "when a property is excluded it does not affect its siblings"() {
		given:
		  def base = new ObjectWithTwoProperties(name: 'about-to-change', description: 'about-to-change')
		  def working = new ObjectWithTwoProperties(name: 'changed', description: 'changed')
		when:
		  def node = objectDifferBuilderPreconfiguredToReturnIgnoredNodes.inclusion()
				  .exclude().propertyNameOfType(ObjectWithTwoProperties, 'description').and()
				  .build()
				  .compare(working, base)
		then:
		  node.childCount() == 2
		  node.getChild('name').isChanged()
		  node.getChild('description').isIgnored()
	}

	def "when a property is excluded it does not affect properties of other types"() {
		given:
		  def base = new ObjectWithTwoProperties(name: 'about-to-change', description: 'about-to-change')
		  def working = new ObjectWithTwoProperties(name: 'changed', description: 'changed')

		and: 'an ObjectDiffer with inclusions for a different object type'
		  def objectDiffer = objectDifferBuilderPreconfiguredToReturnIgnoredNodes
				  .inclusion()
				  .exclude().propertyNameOfType(ObjectWithOneProperty, 'name').and()
				  .build()

		when:
		  def node = objectDiffer.compare(working, base)

		then:
		  node.childCount() == 2
		  node.getChild('name').isChanged()
		  node.getChild('description').isChanged()
	}
}
