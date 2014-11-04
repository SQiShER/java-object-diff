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

import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.*

@Subject(PropertyNameInclusionResolver)
class PropertyNameInclusionResolverTest extends Specification {

	PropertyNameInclusionResolver inclusionResolver = new PropertyNameInclusionResolver()

	def "GetInclusion: returns DEFAULT when node is null"() {
		expect:
		  inclusionResolver.getInclusion(null) == DEFAULT
	}

	def "GetInclusion: returns DEFAULT when no INCLUDE or EXCLUDE has been configured"() {
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
	}

	@Unroll
	def "GetInclusion: returns #expectedInclusion when configured property name inclusion is #propertyNameInclusion"() {
		given:
		  def node = Mock(DiffNode, { getPropertyName() >> 'property-under-test' })
		when:
		  inclusionResolver.setInclusion('some-other-property', otherExistingInclusion)
		  inclusionResolver.setInclusion('property-under-test', propertyNameInclusion)
		then:
		  inclusionResolver.getInclusion(node) == expectedInclusion
		where:
		  propertyNameInclusion | otherExistingInclusion || expectedInclusion
		  null                  | INCLUDED               || DEFAULT
		  DEFAULT               | EXCLUDED               || DEFAULT
		  INCLUDED              | DEFAULT                || INCLUDED
		  EXCLUDED              | DEFAULT                || EXCLUDED
	}

	def "GetInclusion: returns INCLUDED when the node itself is not explicitly included but its parent is"() {
		given:
		  def parentNode = Mock(DiffNode, {
			  getPropertyName() >> 'parent-property'
		  })
		  def node = Mock(DiffNode, {
			  getPropertyName() >> 'child-property'
			  getParentNode() >> parentNode
		  })
		when:
		  inclusionResolver.setInclusion(parentNode.getPropertyName(), INCLUDED)
		then:
		  inclusionResolver.getInclusion(node) == INCLUDED
	}

	def "EnablesStrictIncludeMode: is true when at least one INCLUDE has been configured"() {
		expect:
		  !inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion('property', INCLUDED)
		then:
		  inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion('property', EXCLUDED)
		then:
		  !inclusionResolver.enablesStrictIncludeMode()
	}
}
