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

package de.danielbechler.diff.node

import de.danielbechler.diff.inclusion.Inclusion
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import de.danielbechler.diff.selector.RootElementSelector
import spock.lang.Specification
import spock.lang.Unroll

import static de.danielbechler.diff.inclusion.Inclusion.EXCLUDED
import static de.danielbechler.diff.inclusion.Inclusion.INCLUDED

class ValueNodeTest extends Specification {

	def node = new ValueNode<Inclusion>()

	def 'GetChild: throws an exception when given a RootElementSelector'() {
		when:
		  node.getChild(RootElementSelector.instance)
		then:
		  thrown(IllegalArgumentException)
		where:
		  selector << [
				  RootElementSelector.instance,
				  [RootElementSelector.instance]
		  ]
	}

	def 'GetChild: creates a new child for unknown selector'() {
		given:
		  def childElementSelector = new BeanPropertyElementSelector('foo')

		when: "called with unknown selector"
		  def childNode = node.getChild(childElementSelector)

		then: "returns new child node"
		  childNode.elementSelector == childElementSelector

		and: "the next call returns the same child node"
		  node.getChild(childElementSelector) == childNode
	}

	def 'GetChild: returns the same child on consecutive calls with the same selector'() {
		setup:
		  def childElementSelector = new BeanPropertyElementSelector('foo')

		expect: "called with unknown selector"
		  node.getChild(childElementSelector) is node.getChild(childElementSelector)

		and:
		  node.getChild(childElementSelector).elementSelector == childElementSelector
	}

	def 'GetChild: sets reference to parent when creating new child nodes'() {
		when:
		  def childNode = node.getChild(new BeanPropertyElementSelector('foo'))

		then:
		  node == childNode.parent
	}

	def 'GetChild: creates child node hierarchy for list of ElementSelectors'() {
		when:
		  node.getNodeForPath(NodePath.with('foo', 'bar'))
		then:
		  node.hasChild(new BeanPropertyElementSelector('foo'))
		and:
		  node.getChild(new BeanPropertyElementSelector('foo')).hasChild(new BeanPropertyElementSelector('bar'))
	}

	@Unroll
	def 'HasValue: returns #expected when value is #valueText'() {
		when:
		  node.setValue(value)
		then:
		  node.hasValue() == expected
		where:
		  value    || expected
		  INCLUDED || true
		  null     || false
		  valueText = value == null ? 'not set' : 'set'
	}

	def 'GetNodeForPath: returns root node for root path'() {
		expect:
		  node.getNodeForPath(NodePath.withRoot()) is node
	}

	def 'GetNodeForPath: returns child node for child path'() {
		expect:
		  node.getNodeForPath(NodePath.with('foo')).elementSelector == new BeanPropertyElementSelector('foo')
	}

	def 'GetNodeForPath: always starts at the root node'() {
		given:
		  def childNode = node.getNodeForPath(NodePath.with('foo', 'bar'))

		expect:
		  childNode.getNodeForPath(NodePath.with('foo', 'bar')) is childNode
	}

	def 'ContainsValue: is true when the node has the requested value'() {
		when:
		  node.setValue(INCLUDED)
		then:
		  node.containsValue(INCLUDED)
	}

	def 'ContainsValue: is true when any child node has the requested value'() {
		given:
		  def childNode = node.getChild(new BeanPropertyElementSelector('foo'))
		when:
		  childNode.setValue(INCLUDED)
		then:
		  node.containsValue(INCLUDED)
	}

	def 'ContainsValue: is false when no node has the requested value'() {
		given:
		  def childNode = node.getChild(new BeanPropertyElementSelector('foo'))
		  childNode.setValue(EXCLUDED)
		expect:
		  !node.containsValue(INCLUDED)
	}

	def 'GetValue: returns the assigned value'() {
		when:
		  node.setValue(INCLUDED)
		then:
		  node.getValue() == INCLUDED
		when:
		  node.setValue(EXCLUDED)
		then:
		  node.getValue() == EXCLUDED
	}
}
