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

import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import de.danielbechler.diff.selector.RootElementSelector
import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED

/**
 * Created by Daniel Bechler.
 */
class InclusionNodeSpec extends Specification {

	def node = new InclusionNode()

	def 'getChild creates a child for an unknown selector'() {
		given:
		  def childElementSelector = new BeanPropertyElementSelector('foo')

		when: "called with unknown selector"
		  def childNode = node.getChild(childElementSelector)

		then: "returns new child node"
		  childNode.elementSelector == childElementSelector

		and: "the next call returns the same child node"
		  node.getChild(childElementSelector) == childNode
	}

	def 'getChild returns the same child on consecutive calls with the same selector'() {
		setup:
		  def childElementSelector = new BeanPropertyElementSelector('foo')

		expect: "called with unknown selector"
		  node.getChild(childElementSelector) is node.getChild(childElementSelector)

		and:
		  node.getChild(childElementSelector).elementSelector == childElementSelector
	}

	def 'getChild sets reference to parent when creating new child nodes'() {
		when:
		  def childNode = node.getChild(new BeanPropertyElementSelector('foo'))

		then:
		  node == childNode.parent
	}

	def 'getChild creates child node hierarchy for list of ElementSelectors'() {
		when:
		  node.getNodeForPath(NodePath.with('foo', 'bar'))
		then:
		  node.hasChild(new BeanPropertyElementSelector('foo'))
		and:
		  node.getChild(new BeanPropertyElementSelector('foo')).hasChild(new BeanPropertyElementSelector('bar'))
	}

	@FailsWith(value = IllegalArgumentException)
	def 'getChild throws an exception when called with a RootElementSelector'() {
		expect:
		  node.getChild(RootElementSelector.instance)
	}

	def 'isIncluded returns false when neither the node itself nor any of its children are INCLUDED'() {
		expect:
		  !node.isIncluded()
	}

	def 'isIncluded returns true when the node itself is INCLUDED'() {
		given:
		  node.value = INCLUDED

		expect:
		  node.isIncluded()
	}

	def 'isIncluded returns true when the node has at least one INCLUDED children'() {
		given:
		  node.getNodeForPath(NodePath.with('foo', 'bar')).value = INCLUDED

		expect:
		  node.isIncluded()
	}

	def 'isIncluded returns false when closest parent with inclusion is EXCLUDED'() {
		given:
		  node.getNodeForPath(NodePath.with('a')).value = EXCLUDED
		  node.getNodeForPath(NodePath.with('a', 'b', 'c')).value = INCLUDED

		expect:
		  !node.getNodeForPath(NodePath.with('a', 'b', 'c')).isIncluded()
	}

	def 'isIncluded returns false when inclusion is EXCLUDED'() {
		given:
		  node.getNodeForPath(NodePath.with('a')).value = INCLUDED
		  node.getNodeForPath(NodePath.with('a', 'b', 'c')).value = EXCLUDED

		expect:
		  !node.getNodeForPath(NodePath.with('a', 'b', 'c')).isIncluded()
	}

	def 'isIncluded returns true when closest parent with explicit inclusion is INCLUDED'() {
		given:
		  node.getNodeForPath(NodePath.with('a')).value = INCLUDED

		expect:
		  node.getNodeForPath(NodePath.with('a', 'b', 'c')).isIncluded()
	}

	def 'isIncluded returns false when node has no included children'() {
		given:
		  node.getNodeForPath(NodePath.with('a', 'b')).value = EXCLUDED

		expect:
		  !node.getNodeForPath(NodePath.with('a')).isIncluded()
	}

	def 'isExcluded returns true when the node is EXCLUDED'() {
		given:
		  node.value = EXCLUDED
		expect:
		  node.isExcluded()
	}

	def 'isExcluded returns true when any of the parents is EXCLUDED'() {
		given:
		  node.getNodeForPath(NodePath.with('a')).value = EXCLUDED
		  node.getNodeForPath(NodePath.with('a', 'b', 'c')).value = INCLUDED
		expect:
		  node.getNodeForPath(NodePath.with('a', 'b', 'c')).isExcluded()
	}

	def 'isExcluded returns false when neither a parent nor the node itself is excluded'() {
		expect:
		  !node.getNodeForPath(NodePath.with('a', 'b', 'c')).isExcluded()
	}

	@Unroll
	def 'hasInclusion returns #expected when inclusion is set to #inclusion'() {
		when:
		  node.setValue(inclusion)

		then:
		  node.hasValue() == expected

		where:
		  inclusion || expected
		  INCLUDED  || true
		  EXCLUDED  || true
		  null      || false
	}

	def 'getNodeForPath returns root node for root path'() {
		expect:
		  node.getNodeForPath(NodePath.withRoot()) is node
	}

	def 'getNodeForPath returns child node for child path'() {
		expect:
		  node.getNodeForPath(NodePath.with('foo')).elementSelector == new BeanPropertyElementSelector('foo')
	}

	def 'getNodeForPath always starts at root node'() {
		given:
		  def childNode = node.getNodeForPath(NodePath.with('foo', 'bar'))

		expect:
		  childNode.getNodeForPath(NodePath.with('foo', 'bar')) is childNode
	}
}
