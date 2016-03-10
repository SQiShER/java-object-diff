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

package de.danielbechler.diff.integration

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.mock.ObjectWithCircularReference
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
public class IgnoreIT extends Specification {

	def 'verify that ignore rules with complex paths works properly'() {
		given:
		  def obj1 = new ObjectWithCircularReference("1")
		  def obj2 = new ObjectWithCircularReference("2")
		  def obj3 = new ObjectWithCircularReference("3")
		  obj1.reference = obj2
		  obj2.reference = obj3
		and:
		  def modifiedObj1 = new ObjectWithCircularReference("1")
		  def modifiedObj2 = new ObjectWithCircularReference("2")
		  def modifiedObj3 = new ObjectWithCircularReference("4")
		  modifiedObj1.reference = modifiedObj2
		  modifiedObj2.reference = modifiedObj3
		and:
		  def nodePath = NodePath.with("reference", "reference")
		when:
		  def objectDiffer1 = ObjectDifferBuilder.startBuilding().build()
		  def verification = objectDiffer1.compare(obj1, modifiedObj1)
		then: "verify that the node can be found when it's not excluded"
		  verification.getChild(nodePath).state == DiffNode.State.CHANGED
		  verification.getChild(nodePath).childCount() == 1
		when:
		  def objectDiffer2 = ObjectDifferBuilder.startBuilding().inclusion().exclude().node(nodePath).and().build()
		  def node = objectDiffer2.compare(obj1, modifiedObj1)
		then: "verify that the node can't be found, when it's excluded"
		  node.getChild(nodePath) == null
	}
}
