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

package de.danielbechler.diff.path

import de.danielbechler.diff.selector.BeanPropertyElementSelector
import de.danielbechler.diff.selector.CollectionItemElementSelector
import de.danielbechler.diff.selector.MapKeyElementSelector
import de.danielbechler.diff.selector.RootElementSelector
import spock.lang.Specification
import spock.lang.Unroll

class NodePathTest extends Specification {

	def 'matches'() {
		expect:
		  path.matches(givenPath) == expected

		where:
		  path               | givenPath          || expected
		  NodePath.with('a') | NodePath.with('a') || true
		  NodePath.with('a') | NodePath.with('z') || false
	}

	@Unroll
	def 'isParentOf returns #expected #message'() {
		expect:
		  path.isParentOf(givenPath) == expected

		where:
		  path                    | givenPath                    || expected || message
		  NodePath.with('a')      | NodePath.with('a', 'b')      || true     || "when given path is child"
		  NodePath.with('a', 'b') | NodePath.with('a')           || false    || "when given path is parent"
		  NodePath.with('a')      | NodePath.with('a')           || false    || "when given path is equal"
		  NodePath.with('a', 'b') | NodePath.with('z', 'b', 'c') || false    || 'when given path doesn\'t share the same ancestry'
	}

	@Unroll
	def 'isChildOf returns #expected #message'() {
		expect:
		  path.isChildOf(otherPath) == expected

		where:
		  path                    | otherPath               || expected || message
		  NodePath.with('a', 'b') | NodePath.with('a')      || true     || 'when given path is parent'
		  NodePath.with('a')      | NodePath.with('a', 'b') || false    || 'when given path is child'
		  NodePath.with('a')      | NodePath.with('a')      || false    || 'when given path is equal'
		  NodePath.with('a', 'b') | NodePath.with('z', 'b') || false    || 'when given path doesn\'t share the same ancestry'
	}

	def 'getLastElementSelector returns last element from path'() {
		expect:
		  path.getLastElementSelector() == expected

		where:
		  path                         || expected
		  NodePath.withRoot()          || RootElementSelector.instance
		  NodePath.with('a', 'b', 'c') || new BeanPropertyElementSelector('c')
	}

	def 'equals'() {
		expect:
		  path1.equals(path2) == result
		where:
		  path1                   | path2                   || result
		  NodePath.withRoot()     | NodePath.withRoot()     || true
		  NodePath.with('a')      | NodePath.with('a')      || true
		  NodePath.with('a')      | NodePath.with('a', 'b') || false
		  NodePath.with('a', 'b') | NodePath.with('a')      || false
		  NodePath.with('a')      | NodePath.with('b')      || false
	}

	def 'compareTo'() {
		expect:
		  path1.compareTo(path2) == result
		where:
		  path1                   | path2                   || result
		  NodePath.withRoot()     | NodePath.withRoot()     || 0
		  NodePath.with('a')      | NodePath.with('a')      || 0
		  NodePath.with('a')      | NodePath.with('a', 'b') || -1
		  NodePath.with('a', 'b') | NodePath.with('a')      || 1
		  NodePath.with('a')      | NodePath.with('b')      || 1
	}

	def 'startBuilding without elements'() {
		expect:
		  NodePath.startBuilding().build().elementSelectors == [RootElementSelector.instance]
	}

	def 'startBuilding with element'() {
		def elementSelector = new CollectionItemElementSelector("foo")
		expect:
		  NodePath.startBuilding().element(elementSelector).build().elementSelectors == [
				  RootElementSelector.instance,
				  elementSelector
		  ]
	}

	def 'startBuilding with propertyName'() {
		expect:
		  NodePath.startBuilding().propertyName("foo", "bar").build().elementSelectors == [
				  RootElementSelector.getInstance(),
				  new BeanPropertyElementSelector("foo"),
				  new BeanPropertyElementSelector("bar")
		  ]
	}

	def 'startBuilding with mapKey'() {
		expect:
		  NodePath.startBuilding().mapKey("foo").build().elementSelectors == [
				  RootElementSelector.instance,
				  new MapKeyElementSelector("foo")
		  ]
	}

	def 'startBuilding with mapKey throws IllegalArgumentException when key is null'() {
		when:
		  NodePath.startBuilding().mapKey(null).build()
		then:
		  thrown IllegalArgumentException
	}

	def 'startBuilding with collectionItem'() {
		expect:
		  NodePath.startBuilding().collectionItem("foo").build().elementSelectors == [
				  RootElementSelector.instance,
				  new CollectionItemElementSelector("foo")
		  ]
	}

	def 'startBuildingFrom'() {
		expect:
		  NodePath.startBuildingFrom(NodePath.with("foo")).build().elementSelectors == [
				  RootElementSelector.getInstance(),
				  new BeanPropertyElementSelector("foo")
		  ]
	}

	def 'startBuildingFrom: throws IllegalArgumentException when called with null'() {
		when:
		  NodePath.startBuildingFrom(null).build()
		then:
		  thrown IllegalArgumentException
	}

	def 'withRoot: build path with only the root element'() {
		expect:
		  NodePath.withRoot().elementSelectors == [
				  RootElementSelector.instance
		  ]
	}
}