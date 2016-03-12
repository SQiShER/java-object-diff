/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.util

import spock.lang.Specification
import spock.lang.Unroll

class CollectionsTest extends Specification {

	def "setOf: should create a new set from the given input"() {
		given:
		  def input = ['a', 'b', 'c']
		expect:
		  Collections.setOf(input) == input as Set
		and:
		  !Collections.setOf(input).is(input)
	}

	def "setOf: should fail with exception when input is null"() {
		when:
		  Collections.setOf(null)
		then:
		  thrown(NullPointerException)
	}

	@Unroll
	def "isEmpty: should return #result for collection #collection"() {
		expect:
		  Collections.isEmpty(collection) == result
		where:
		  collection                             || result
		  null                                   || true
		  []                                     || true
		  ['with', 'one', 'or', 'more', 'items'] || false
	}

	@Unroll
	def "containsAny: should return #result for #needles in #haystack"() {
		expect:
		  Collections.containsAny(haystack, needles) == result
		where:
		  haystack       | needles || result
		  null           | null    || false
		  []             | []      || false
		  []             | ['foo'] || false
		  ['foo']        | []      || false
		  ['foo']        | ['foo'] || true
		  ['foo', 'bar'] | ['bar'] || true

	}

	@Unroll
	def "filteredCopyOf: should return #result for source #source and filter #filter"() {
		expect:
		  Collections.filteredCopyOf(source, filter) == result
		where:
		  source                | filter       || result
		  null                  | null         || []
		  []                    | null         || []
		  []                    | []           || []
		  []                    | ['no match'] || []
		  ['match']             | ['match']    || []
		  ['match', 'no match'] | ['match']    || ['no match']
		  ['foo', 'bar']        | []           || ['foo', 'bar']
	}

	@Unroll
	def 'firstElementOf: should return #result for collection #collection'() {
		expect:
		  Collections.firstElementOf(collection) == result
		where:
		  collection || result
		  null       || null
		  []         || null
		  [1, 2, 3]  || 1
	}
}
