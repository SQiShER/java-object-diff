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

import de.danielbechler.diff.category.CategoryResolver
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Subject

import static de.danielbechler.diff.inclusion.Inclusion.*

@Subject(CategoryInclusionResolver)
class CategoryInclusionResolverTest extends Specification {

	CategoryResolver categoryResolver = Mock CategoryResolver
	CategoryInclusionResolver inclusionResolver = new CategoryInclusionResolver(categoryResolver)

	def "Constructor: fails without categoryResolver"() {
		when:
		  new CategoryInclusionResolver(null)
		then:
		  thrown(IllegalArgumentException)
	}

	def "GetInclusion: returns DEFAULT when no INCLUDE or EXCLUDE has been configured"() {
		given:
		  inclusionResolver.setInclusion('some-category', configuredInclusion)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
		where:
		  configuredInclusion << [null, DEFAULT]
	}

	def "GetInclusion: returns DEFAULT when node has no categories"() {
		given:
		  categoryResolver.resolveCategories(_ as DiffNode) >> []
		and:
		  inclusionResolver.setInclusion('inclusion-to-activate-the-resolver', INCLUDED)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
	}

	def "GetInclusion: returns DEFAULT when none of the nodes categories has inclusion"() {
		given:
		  categoryResolver.resolveCategories(_ as DiffNode) >> ['foo', 'bar']
		and:
		  inclusionResolver.setInclusion('inclusion-to-activate-the-resolver', EXCLUDED)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == DEFAULT
	}

	def "GetInclusion: returns EXCLUDED when any of the nodes categories is EXCLUDED"() {
		given:
		  categoryResolver.resolveCategories(_ as DiffNode) >> ['foo', 'bar']
		and:
		  inclusionResolver.setInclusion('foo', INCLUDED)
		  inclusionResolver.setInclusion('bar', EXCLUDED)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == EXCLUDED
	}

	def "GetInclusion: returns INCLUDED when any of the nodes categories is INCLUDED"() {
		given:
		  categoryResolver.resolveCategories(_ as DiffNode) >> ['foo', 'bar']
		and:
		  inclusionResolver.setInclusion('bar', INCLUDED)
		expect:
		  inclusionResolver.getInclusion(Mock(DiffNode)) == INCLUDED
	}

	def "EnablesStrictIncludeMode: is true when at least one INCLUDE has been configured"() {
		expect:
		  !inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion('category', INCLUDED)
		then:
		  inclusionResolver.enablesStrictIncludeMode()

		when:
		  inclusionResolver.setInclusion('category', EXCLUDED)
		then:
		  !inclusionResolver.enablesStrictIncludeMode()
	}
}
