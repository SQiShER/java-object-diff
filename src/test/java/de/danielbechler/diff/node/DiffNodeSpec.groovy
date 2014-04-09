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

import de.danielbechler.diff.CollectionItemAccessor
import de.danielbechler.diff.nodepath.NodePath
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class DiffNodeSpec extends Specification {
	def rootNode = new DiffNode()
	def childNodeA = new DiffNode(rootNode, new CollectionItemAccessor('A'), null)
	def childNodeB = new DiffNode(rootNode, new CollectionItemAccessor('B'), null)

	def setup() {
		rootNode.addChild(childNodeA)
		rootNode.addChild(childNodeB)
	}

	def 'getChild(NodePath) always starts at root node'() {
		expect:
		  childNodeA.getChild(NodePath.startBuilding().collectionItem('B').build()) == childNodeB
	}
}
