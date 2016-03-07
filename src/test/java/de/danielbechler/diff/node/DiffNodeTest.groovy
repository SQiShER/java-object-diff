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

import de.danielbechler.diff.access.Accessor
import de.danielbechler.diff.access.CollectionItemAccessor
import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.mock.ObjectDiffTest
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification
import spock.lang.Unroll

import static de.danielbechler.diff.node.DiffNode.State.*

class DiffNodeTest extends Specification {

	def 'getChild: always starts at root node when called with NodePath'() {
		given:
		  def rootNode = DiffNode.newRootNode()
		  def childNodeA = new DiffNode(null, new CollectionItemAccessor('A'), null)
		  def childNodeB = new DiffNode(null, new CollectionItemAccessor('B'), null)
		when:
		  rootNode.addChild(childNodeA)
		  childNodeA.addChild(childNodeB)
		then:
		  childNodeA.getChild(NodePath.startBuilding().collectionItem('A').collectionItem('B').build()) == childNodeB
	}

	@Unroll
	def '#stateCheckMethodName: returns true when state is #state'() {
		given:
		  def node = DiffNode.newRootNode()
		  node.state = state
		expect:
		  node.invokeMethod stateCheckMethodName, null
		where:
		  state << [UNTOUCHED, IGNORED, CIRCULAR, CHANGED, REMOVED, ADDED]
		  stateCheckMethodName = 'is' + state.name().toLowerCase().capitalize()
	}

	@Unroll
	def 'hasChanges: returns #expectedHasChangesResult when node state is #state'() {
		given:
		  DiffNode node = DiffNode.newRootNode()
		  node.state = state
		expect:
		  node.hasChanges() == expectedHasChangesResult
		where:
		  state     || expectedHasChangesResult
		  ADDED     || true
		  REMOVED   || true
		  CHANGED   || true
		  UNTOUCHED || false
		  IGNORED   || false
		  CIRCULAR  || false
	}

	def 'hasChanges returns true when any child node has changes'() {
		given:
		  DiffNode rootNode = DiffNode.newRootNode()
		expect:
		  rootNode.hasChanges() == false
		when:
		  rootNode.addChild(Mock(DiffNode, {
			  1 * hasChanges() >> true
		  }))
		then:
		  rootNode.hasChanges() == true
	}

	def 'getPropertyPath: returns absolute path for root node'() {
		given:
		  def diffNode = DiffNode.newRootNode()

		expect:
		  diffNode.path == NodePath.withRoot()
	}


	def 'getPropertyPath: returns absolute path for child node'() {
		given:
		  def parentNode = Mock(DiffNode) {
			  getPath() >> NodePath.startBuilding().propertyName('a', 'b').build()
		  }
		  def accessor = Mock(Accessor) {
			  getElementSelector() >> new BeanPropertyElementSelector('c')
		  }
		  def diffNode = new DiffNode(parentNode, accessor, Object)

		expect:
		  diffNode.path == NodePath.with('a', 'b', 'c')
	}

	def 'addChild: fails with exception when attempting to add root node'() {
		given:
		  def rootNode = DiffNode.newRootNode()
		  def anotherRootNode = DiffNode.newRootNode()
		when:
		  rootNode.addChild(anotherRootNode)
		then:
		  thrown IllegalArgumentException
	}

	def 'addChild: fails with exception when attempting to add node that is already child of another node'() {
		given:
		  def childNode = new DiffNode(DiffNode.newRootNode(), Stub(Accessor), Object)
		when: 'adding the child to another node'
		  DiffNode.newRootNode().addChild(childNode)
		then:
		  thrown IllegalArgumentException
	}

	def 'addChild: fails with exception when attempting to add node to itself'() {
		given:
		  def node = DiffNode.newRootNode()
		when:
		  node.addChild node
		then:
		  thrown IllegalArgumentException
	}

	def 'addChild: establishes parent-child relationship'() {
		given:
		  def parent = DiffNode.newRootNode()
		and:
		  def childAccessor = Stub Accessor, {
			  getElementSelector() >> new BeanPropertyElementSelector('foo')
		  }
		  def child = new DiffNode(null, childAccessor, String)
		when:
		  parent.addChild(child)
		then: 'child has been added to parent'
		  parent.getChild(child.elementSelector).is child
		and: 'parent has been assigned to child'
		  child.parentNode.is parent
	}

	def 'addChild: changes parent node state to CHANGED if child node has changes'() {
		given:
		  def parentNode = DiffNode.newRootNode()
		and:
		  def childNode = new DiffNode(parentNode, Stub(Accessor), String)
		  childNode.setState(CHANGED)
		expect:
		  parentNode.state == UNTOUCHED
		and:
		  childNode.hasChanges() == true
		when:
		  parentNode.addChild(childNode)
		then:
		  parentNode.state == CHANGED
	}

	def 'getPropertyAnnotations: delegates to accessor if it is property aware'() {
		given:
		  ObjectDiffTest annotation = Stub(ObjectDiffTest)
		  PropertyAwareAccessor accessor = Stub(PropertyAwareAccessor) {
			  getReadMethodAnnotations() >> [annotation]
		  }
		  def node = new DiffNode(null, accessor, Object)
		expect:
		  node.propertyAnnotations.size() == 1
		  node.propertyAnnotations.contains(annotation)
	}

	def 'getPropertyAnnotations: returns empty set if accessor is not property aware'() {
		given:
		  def node = new DiffNode(null, Stub(Accessor), Object)
		expect:
		  node.propertyAnnotations.isEmpty()
	}

	def 'getPropertyAnnotation: should delegate call to property accessor'() {
		given:
		  ObjectDiffTest annotation = Mock(ObjectDiffTest)
		  PropertyAwareAccessor accessor = Mock(PropertyAwareAccessor)
		when:
		  def node = new DiffNode(DiffNode.newRootNode(), accessor)
		  node.getPropertyAnnotation(ObjectDiffTest) == annotation
		then:
		  1 * accessor.getReadMethodAnnotation(ObjectDiffTest) >> annotation
	}

	def 'getPropertyAnnotation: should return null if accessor is not a property aware accessor'() {
		given:
		  def accessor = Mock(Accessor)
		expect:
		  def node = new DiffNode(null, accessor, Object)
		  node.getPropertyAnnotation(ObjectDiffTest) == null
	}

	def 'getPropertyName: returns name from PropertyAwareAccessor'() {
		given:
		  def expectedPropertyName = 'foo';
		  def nodeWithPropertyName = new DiffNode(null, Stub(PropertyAwareAccessor, {
			  getPropertyName() >> expectedPropertyName
		  }), Object)
		expect:
		  nodeWithPropertyName.propertyName == expectedPropertyName
	}

	def 'getPropertyName: returns name of parent node if it doesn\'t have one itself'() {
		given:
		  def expectedPropertyName = 'foo'
		  def parentNodeWithPropertyName = new DiffNode(null, Stub(PropertyAwareAccessor, {
			  getPropertyName() >> expectedPropertyName
		  }), Object)
		and:
		  def nodeWithoutPropertyName = new DiffNode(parentNodeWithPropertyName, Stub(Accessor), Object)
		expect:
		  nodeWithoutPropertyName.propertyName == expectedPropertyName
	}

	def 'getPropertyName: returns null when property name can not be resolved from accessor'() {
		expect:
		  def node = new DiffNode(null, Mock(Accessor), Object)
		  node.propertyName == null
	}

	@Unroll
	def 'isPropertyAware: returns #expectedResult when acessor #doesOrDoesNotImplement PropertyAwareAccessor interface'() {
		given:
		  def node = new DiffNode(null, Stub(accessorType), Object)
		expect:
		  node.isPropertyAware() == expectedResult
		where:
		  accessorType          || expectedResult
		  Accessor              || false
		  PropertyAwareAccessor || true

		  doesOrDoesNotImplement = expectedResult ? 'implements' : 'does not implement'
	}

	def "should return added categories"() {
		given:
		  def node = new DiffNode(null, Mock(Accessor), Object)
		  node.addCategories(["addedCategory"] as List)
		expect:
		  node.getCategories() == ["addedCategory"] as Set
	}

	def "categories should not be modifiable by a client directly"() {

		when:
		  def node = new DiffNode(null, Mock(Accessor), Object)
		  def cats = node.getCategories()
		  cats.removeAll()
		then:
		  thrown UnsupportedOperationException
	}

	def "should throw exception when added a null collection"() {

		when:
		  def node = new DiffNode(null, Mock(Accessor), Object)
		  node.addCategories(null)
		then:
		  def ex = thrown(IllegalArgumentException)
		  ex.message == "'additionalCategories' must not be null"
	}
}
