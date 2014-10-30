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

import de.danielbechler.diff.access.Accessor
import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.*

@Subject(TypePropertyConfigInclusionResolver)
class TypePropertyConfigInclusionResolverTest extends Specification {

	def inclusionResolver = new TypePropertyConfigInclusionResolver()

	def 'should return DEFAULT when node is not property aware'() {
		given:
		  def node = new DiffNode(Stub(Accessor), null)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	def 'should return DEFAULT when node has no parent'() {
		given:
		  def propertyAwareAccessor = Stub(PropertyAwareAccessor)
		  def node = new DiffNode(propertyAwareAccessor, null)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	def 'should return DEFAULT when parent node has no type'() {
		given:
		  def propertyAwareAccessor = Stub(PropertyAwareAccessor)
		  def parentNode = new DiffNode(null)
		  def node = new DiffNode(parentNode, propertyAwareAccessor, null)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	def "should return DEFAULT when node doesn't have a property name"() {
		given:
		  def node = createPropertyNode(ObjectForTesting, null)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	@Unroll
	def 'should return #expected when inclusion for node is #inclusion'() {
		given:
		  def node = createPropertyNode(ObjectForTesting, 'foo')
		and:
		  inclusionResolver.setInclusion(ObjectForTesting, 'foo', inclusion)
		expect:
		  inclusionResolver.getInclusion(node) == expected
		where:
		  inclusion || expected
		  null      || DEFAULT
		  DEFAULT   || DEFAULT
		  INCLUDED  || INCLUDED
		  EXCLUDED  || EXCLUDED
	}

	def 'should return EXCLUDED when the nodes inclusion is DEFAULT but any of its siblings are INCLUDED'() {
		given:
		  def node = createPropertyNode(ObjectForTesting, 'foo')
		and:
		  inclusionResolver.setInclusion(ObjectForTesting, 'bar', INCLUDED)
		expect:
		  inclusionResolver.getInclusion(node) == EXCLUDED
	}

	def 'should return DEFAULT when the node has siblings with inclusions but none if them is INCLUDED'() {
		given:
		  def node = createPropertyNode(ObjectForTesting, 'foo')
		and:
		  inclusionResolver.setInclusion(ObjectForTesting, 'bar', inclusion)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
		where:
		  inclusion << [null, DEFAULT, EXCLUDED]
	}

	def 'should not be affected by inclusions from other parent types'() {
		given:
		  def node = createPropertyNode(ObjectForTesting, 'foo')
		and:
		  inclusionResolver.setInclusion(ObjectForTesting, 'foo', DEFAULT)
		  inclusionResolver.setInclusion(Object, 'foo', EXCLUDED)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	DiffNode createPropertyNode(Class<?> parentType, String propertyName) {
		def parentNode = new DiffNode(parentType)
		def accessor = Stub(PropertyAwareAccessor, { getPropertyName() >> propertyName })
		return new DiffNode(parentNode, accessor, null)
	}

	class ObjectForTesting {
		def foo
	}
}
