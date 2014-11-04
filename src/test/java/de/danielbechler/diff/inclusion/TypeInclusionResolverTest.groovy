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

@Subject(TypeInclusionResolver)
class TypeInclusionResolverTest extends Specification {

	TypeInclusionResolver inclusionResolver = new TypeInclusionResolver()

	def "GetInclusion: returns DEFAULT when no INCLUDE or EXCLUDE has been configured"() {
		given:
		  inclusionResolver.setInclusion(Object, configuredInclusion)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
		where:
		  configuredInclusion << [null, DEFAULT]
	}

	def "GetInclusion: returns DEFAULT when node has no valueType"() {
		given:
		  def node = Mock(DiffNode, { getValueType() >> null })
		and:
		  inclusionResolver.setInclusion(String, INCLUDED)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	def "GetInclusion: returns DEFAULT when no inclusion for the nodes valueType has been configured"() {
		given:
		  def node = Mock(DiffNode, { getValueType() >> Date })
		and:
		  inclusionResolver.setInclusion(String, INCLUDED)
		expect:
		  inclusionResolver.getInclusion(node) == DEFAULT
	}

	@Unroll
	def "GetInclusion: returns #expectedInclusion when the configured inclusion for the nodes valueType is #configuredInclusion"() {
		given:
		  def valueType = String
		  def node = Mock(DiffNode, { getValueType() >> valueType })
		and: 'ensure the inclusion resolver is "active"'
		  inclusionResolver.setInclusion(Object, otherInclusionToActivateResolver)
		and:
		  inclusionResolver.setInclusion(valueType, configuredInclusion)
		expect:
		  inclusionResolver.getInclusion(node) == expectedInclusion
		where:
		  otherInclusionToActivateResolver | configuredInclusion || expectedInclusion
		  INCLUDED                         | null                || DEFAULT
		  EXCLUDED                         | DEFAULT             || DEFAULT
		  DEFAULT                          | INCLUDED            || INCLUDED
		  DEFAULT                          | EXCLUDED            || EXCLUDED
	}

	def "EnablesStrictIncludeMode: is true when at least one INCLUDE has been configured"() {
		expect:
		  !inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion(Object, INCLUDED)
		then:
		  inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion(Object, EXCLUDED)
		then:
		  !inclusionResolver.enablesStrictIncludeMode()
	}
}
