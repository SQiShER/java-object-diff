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

package de.danielbechler.diff.integration

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
/**
 * @author Daniel Bechler
 */
public class DeepDiffingCollectionItemChangeIT extends Specification {

	def 'returns full property graph of added collection items'() {
		given:
		  def base = [:]
		  def working = [foo: new ObjectWithString('bar')]
		when:
		  def objectDiffer = ObjectDifferBuilder.startBuilding().build()
		  def node = objectDiffer.compare(working, base)
		then:
		  def fooMapEntryPath = NodePath.startBuilding().mapKey("foo").build()
		  node.getChild(fooMapEntryPath).state == DiffNode.State.ADDED
		and:
		  def fooMapEntryValuePath = NodePath.startBuildingFrom(fooMapEntryPath).propertyName('value').build()
		  node.getChild(fooMapEntryValuePath).state == DiffNode.State.ADDED
	}
}
