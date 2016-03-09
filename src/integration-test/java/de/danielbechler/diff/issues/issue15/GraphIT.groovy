/*
 * Copyright 2014 Daniel Bechler
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
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

package de.danielbechler.diff.issues.issue15

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.circular.CircularReferenceDetector
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.PrintingVisitor
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

import static de.danielbechler.diff.path.NodePath.startBuilding

/**
 * This tests have been provided by a user who had some trouble with circular relationships. The introduction
 * of the {@link CircularReferenceDetector} fixed his problems. To avoid regression, these integration tests
 * will be kept until other tests cover the same scenarios.
 *
 * @author https://github.com/oplohmann (original author)
 * @author Daniel Bechler (modifications)
 */
public class GraphIT extends Specification {

	private static final boolean PRINT_ENABLED = true

	private static DiffNode compareAndPrint(GraphNode modified, GraphNode base) {
		DiffNode root = ObjectDifferBuilder.buildDefault().compare(modified, base)
		if (PRINT_ENABLED) {
			root.visit(new PrintingVisitor(modified, base))
		}
		return root
	}

	def basicNode() {
		given:
		  GraphNode base = new GraphNode(1)
		  GraphNode a = new GraphNode(2, "a")
		  base.directReference = a
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  modified.directReference = modifiedA
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.childCount() == 1
		  root.getChild('directReference').getChild('value').state == DiffNode.State.CHANGED
		  root.getChild('directReference').getChild('value').childCount() == 0
	}

	def basicNodeWithDirectReferences() {
		given:
		  GraphNode base = new GraphNode(1)
		  GraphNode a = new GraphNode(2, "a")
		  base.directReference = a
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  modified.directReference = modifiedA
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.childCount() == 1
		  root.getChild('directReference').getChild('value').state == DiffNode.State.CHANGED
		  root.getChild('directReference').getChild('value').childCount() == 0
	}

	def basicBidirectionalWithChildren() {
		given:
		  GraphNode base = new GraphNode(1)
		  GraphNode a = new GraphNode(2, "a")
		  GraphNode b = new GraphNode(3, "b")
		  base.addChildren(a, b)
		  a.directReference = b
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  GraphNode modifiedB = new GraphNode(3, "by")
		  modified.addChildren(modifiedA, modifiedB)
		  modifiedA.directReference = modifiedB
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.childCount() == 1
		  root.getChild('children').childCount() == 2
		  root.getChild(startBuilding().propertyName('children').collectionItem(a).build()).state == DiffNode.State.CHANGED
		  root.getChild(startBuilding().propertyName('children').collectionItem(b).build()).state == DiffNode.State.CHANGED
	}


	def basicBidirectionalWithChildrenAndMaps() {
		given:
		  GraphNode a = new GraphNode(2, "a")
		  GraphNode b = new GraphNode(3, "b")
		  establishCircularDirectReference(a, b)
		  a.getMap().put("node-b", b)
		  a.getMap().put("node-b-again", b)
		  b.getMap().put("node-a", a)
		and:
		  GraphNode base = new GraphNode(1)
		  base.getMap().put("a", "a")
		  base.getChildren().add(b)
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  modified.getMap().put("a", "az")
		  GraphNode modifiedB = new GraphNode(3, "by")
		  modified.addChild(modifiedB)
		  establishCircularDirectReference(modifiedA, modifiedB)
		  modifiedA.getMap().put("node-b", modifiedB)
		  modifiedB.getMap().put("node-a", modifiedA)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.getChild(nodePath.build())?.state == expectedState
		where:
		  nodePath                                                                                                                                                  || expectedState
		  startBuilding().propertyName("map").mapKey("a")                                                                                                           || DiffNode.State.CHANGED
		  startBuilding().propertyName("children").collectionItem(new GraphNode(3)).propertyName("value")                                                           || DiffNode.State.CHANGED
		  startBuilding().propertyName("children").collectionItem(new GraphNode(3)).propertyName("map").mapKey("node-a").propertyName("value")                      || DiffNode.State.CHANGED
		  startBuilding().propertyName("children").collectionItem(new GraphNode(3)).propertyName("map").mapKey("node-a").propertyName("map").mapKey("node-b-again") || DiffNode.State.CIRCULAR
		  startBuilding().propertyName("children").collectionItem(new GraphNode(3)).propertyName("directReference").propertyName("value")                           || DiffNode.State.CHANGED
		  startBuilding().propertyName("children").collectionItem(new GraphNode(3)).propertyName("directReference").propertyName("map").mapKey("node-b-again")      || DiffNode.State.CIRCULAR
	}

	def basicBidirectionalWithoutChildren() {
		given:
		  GraphNode a = new GraphNode(1, "a")
		  GraphNode b = new GraphNode(2, "b")
		  a.directReference = b
		and:
		  GraphNode modifiedA = new GraphNode(1, "ax")
		  GraphNode modifiedB = new GraphNode(2, "by")
		  modifiedA.directReference = modifiedB
		when:
		  DiffNode root = compareAndPrint(modifiedA, a)
		then:
		  root.getChild("directReference").getChild("value").state == DiffNode.State.CHANGED
		  root.getChild("directReference").getChild("value").childCount() == 0
		and:
		  root.getChild("value").state == DiffNode.State.CHANGED
		  root.getChild("value").childCount() == 0
	}

	def basicNodeWithDirectReferences2() {
		given:
		  GraphNode base = new GraphNode("base")
		  GraphNode a = new GraphNode("a")
		  base.directReference = a
		and:
		  GraphNode modified = new GraphNode("modified")
		  GraphNode modifiedA = new GraphNode("ax")
		  modified.directReference = modifiedA
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.getChild("directReference").getChild("value").state == DiffNode.State.CHANGED
		  root.getChild("directReference").getChild("value").childCount() == 0
		and:
		  root.getChild("value").state == DiffNode.State.CHANGED
		  root.getChild("value").childCount() == 0
	}

	/**
	 * Does not detect any changes since no primary key defined for each node
	 */
	def basicBidirectionalNodeWithChildNodes() {
		given:
		  GraphNode base = new GraphNode()
		  GraphNode a = new GraphNode("a")
		  GraphNode b = new GraphNode("b")
		  base.getChildren().add(a)
		  base.getChildren().add(b)
		and:
		  GraphNode modified = new GraphNode()
		  GraphNode modifiedA = new GraphNode("a")
		  GraphNode modifiedB = new GraphNode("bx")
		  modified.getChildren().add(modifiedA)
		  modified.getChildren().add(modifiedB)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.isUntouched() == true
		and:
		  // NOTE: This is expected, since Collections (and java-object-diff) rely heavily on the proper
		  // implementation of hashCode and equals. The GraphNode uses the ID as sole criteria in it's
		  // equality check. Since all instances created without an explicit ID, will default to -1, all
		  // GraphNodes in this example are technically equal.
		  //
		  // The following test proves this:
		  GraphNode actualA = base.children.get(0)
		  GraphNode actualB = base.children.get(1)
		  actualA == a
		  actualB == b
		  actualA == actualB
	}

	def basicBidirectionalNodeWithChildNodesWithIds() {
		given:
		  GraphNode a = new GraphNode(2, "a")
		  GraphNode b = new GraphNode(3, "b")
		  GraphNode base = new GraphNode(1)
		  base.addChild(a)
		  base.addChild(b)
		and:
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  GraphNode modifiedB = new GraphNode(3, "by")
		  GraphNode modified = new GraphNode(1)
		  modified.addChild(modifiedA)
		  modified.addChild(modifiedB)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.getChild(startBuilding().propertyName("children").collectionItem(a).build()).state == DiffNode.State.CHANGED
		  root.getChild(startBuilding().propertyName("children").collectionItem(a).build()).childCount() == 1
		and:
		  root.getChild(startBuilding().propertyName("children").collectionItem(b).build()).state == DiffNode.State.CHANGED
		  root.getChild(startBuilding().propertyName("children").collectionItem(b).build()).childCount() == 1
	}

	def simpleGraphWithChanges() {
		given:
		  GraphNode base = new GraphNode(1)
		  GraphNode a = new GraphNode(2, "a")
		  a.parent = base
		  base.addChild(a)
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "ax")
		  modifiedA.parent = modified
		  modified.addChild(modifiedA)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.getChild(startBuilding().propertyName("children").collectionItem(modifiedA).propertyName("value").build()).state == DiffNode.State.CHANGED
	}

	def simpleGraphWithoutChanges() {
		given:
		  GraphNode base = new GraphNode(1)
		  GraphNode a = new GraphNode(2, "a")
		  a.parent = base
		  base.addChild(a)
		and:
		  GraphNode modified = new GraphNode(1)
		  GraphNode modifiedA = new GraphNode(2, "a")
		  modifiedA.parent = modified
		  modified.addChild(modifiedA)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.isUntouched()
	}

	def simpleGraphExtended() {
		given:
		  GraphNode a = new GraphNode(2, "a")
		  GraphNode b = new GraphNode(3, "b")
		  GraphNode base = new GraphNode(1)
		  a.parent = base
		  base.addChild(a)
		  b.parent = base
		  base.addChild(b)
		and:
		  GraphNode modifiedA = new GraphNode(2, "a")
		  GraphNode modifiedB = new GraphNode(3, "bx")
		  GraphNode modified = new GraphNode(1)
		  modifiedA.parent = modified
		  modified.addChild(modifiedA)
		  modifiedB.parent = modified
		  modified.addChild(modifiedB)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  root.getChild(startBuilding().propertyName("children").collectionItem(b).propertyName("value").build()).state == DiffNode.State.CHANGED
	}

	private static void establishCircularDirectReference(GraphNode a, GraphNode b) {
		a.setDirectReference(b)
		b.setDirectReference(a)
	}

	private static void establishCircularChildRelationship(GraphNode a, GraphNode b) {
		a.addChild(b)
		b.addChild(a)
	}

	private static void establishParentChildRelationship(GraphNode parent, GraphNode child) {
		child.setParent(parent)
		parent.addChild(child)
	}

	def bidirectionalGraphStackOverflow() {
		given:
		  GraphNode a = new GraphNode(2, "a")
		  GraphNode b = new GraphNode(3, "b")
		  establishCircularDirectReference(a, b)
		and:
		  GraphNode base = new GraphNode(1)
		  establishParentChildRelationship(base, a)
		  establishParentChildRelationship(base, b)
		and:
		  GraphNode aa = new GraphNode(4, "aa")
		  GraphNode ba = new GraphNode(5, "ba")
		  establishCircularChildRelationship(aa, ba)
		and:
		  establishParentChildRelationship(a, aa)
		  establishParentChildRelationship(b, ba)
		and:
		  GraphNode baa = new GraphNode(6, "baa")
		  establishParentChildRelationship(ba, baa)
		and:
		  GraphNode modifiedA = new GraphNode(2, "a")
		  GraphNode modifiedB = new GraphNode(3, "b")
		  establishCircularDirectReference(modifiedA, modifiedB)
		and:
		  GraphNode modified = new GraphNode(1)
		  establishParentChildRelationship(modified, modifiedA)
		  establishParentChildRelationship(modified, modifiedB)
		and:
		  GraphNode modifiedAA = new GraphNode(4, "aa")
		  GraphNode modifiedBA = new GraphNode(5, "ba-x")
		  establishCircularChildRelationship(modifiedAA, modifiedBA)
		and:
		  establishParentChildRelationship(modifiedA, modifiedAA)
		  establishParentChildRelationship(modifiedB, modifiedBA)
		and:
		  GraphNode modifieBAA = new GraphNode(6, "baa-y")
		  establishParentChildRelationship(modifiedBA, modifieBAA)
		when:
		  DiffNode root = compareAndPrint(modified, base)
		then:
		  def nodePathExpectedToBechanged = [
				  startBuilding().propertyName("children").collectionItem(a).propertyName("children").collectionItem(aa).propertyName("children").collectionItem(ba).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(a).propertyName("children").collectionItem(aa).propertyName("children").collectionItem(ba).propertyName("children").collectionItem(baa).build(),
				  startBuilding().propertyName("children").collectionItem(a).propertyName("directReference").propertyName("children").collectionItem(ba).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(a).propertyName("directReference").propertyName("children").collectionItem(ba).propertyName("children").collectionItem(baa).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(b).propertyName("children").collectionItem(ba).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(b).propertyName("children").collectionItem(ba).propertyName("children").collectionItem(baa).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(b).propertyName("directReference").propertyName("children").collectionItem(aa).propertyName("children").collectionItem(ba).propertyName("value").build(),
				  startBuilding().propertyName("children").collectionItem(b).propertyName("directReference").propertyName("children").collectionItem(aa).propertyName("children").collectionItem(ba).propertyName("children").collectionItem(baa).propertyName("value").build(),
		  ]
		  nodePathExpectedToBechanged.each { NodePath nodePath ->
			  assert root.getChild(nodePath)?.state == DiffNode.State.CHANGED
		  }
	}


	def testWithSimpleBiDirectionalConnection() {
		given:
		  GraphNode working1 = new GraphNode(1, "foo")
		  GraphNode working2 = new GraphNode(2, "bar")
		  establishCircularDirectReference(working1, working2)
		and:
		  GraphNode base1 = new GraphNode(1)
		  GraphNode base2 = new GraphNode(2)
		  establishCircularDirectReference(base1, base2)
		when:
		  DiffNode node = ObjectDifferBuilder.startBuilding()
				  .filtering()
				  .returnNodesWithState(DiffNode.State.CIRCULAR).and()
				  .build()
				  .compare(working1, base1)
		then:
		  node.getChild('value').state == DiffNode.State.ADDED
		  node.getChild('directReference').state == DiffNode.State.CHANGED
		  node.getChild('directReference').getChild('value').state == DiffNode.State.ADDED
		  node.getChild('directReference').getChild('directReference').isCircular()
	}

	def testWithMapBasedBiDirectionalConnection() {
		given:
		  GraphNode working1 = new GraphNode(1, "foo")
		  GraphNode working2 = new GraphNode(2, "bar")
		  working1.getMap().put("foo", working2)
		  working2.getMap().put("bar", working1)
		and:
		  GraphNode base1 = new GraphNode(1)
		  GraphNode base2 = new GraphNode(2)
		  base1.getMap().put("foo", base2)
		  base2.getMap().put("bar", base1)
		and:
		  ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding()
		  objectDifferBuilder.filtering().returnNodesWithState(DiffNode.State.CIRCULAR)
		  ObjectDiffer differ = objectDifferBuilder.build()
		when:
		  DiffNode node = differ.compare(working1, base1)
		then:
		  node.getChild(startBuilding().propertyName("map").mapKey("foo").propertyName("map").mapKey("bar").build()).isCircular()
	}
}
