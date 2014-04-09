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

package de.danielbechler.diff.nodepath

import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by Daniel Bechler.
 */
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
}
