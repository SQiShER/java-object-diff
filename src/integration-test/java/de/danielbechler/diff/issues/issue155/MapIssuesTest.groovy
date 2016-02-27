/*
 * Copyright 2016 Daniel Bechler
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

package de.danielbechler.diff.issues.issue155

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.identity.IdentityStrategy
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import de.danielbechler.diff.selector.MapKeyElementSelector
import spock.lang.Specification

public class MapIssuesTest extends Specification {

	static class LocationIdentityStrategy implements IdentityStrategy {

		@Override
		boolean equals(workingLocation, baseLocation) {
			if (workingLocation.city == baseLocation.city) {
				return true
			}
			return false
		}
	}

	static class LeafNodeCountingVisitor implements DiffNode.Visitor {
		int leafNodeCount = 0

		@Override
		void node(DiffNode node, Visit visit) {
			if (node.childCount() == 0) {
				leafNodeCount++;
			}
		}
	}

	def 'compare with collections'() {
		given:
		  def sharedCity = 'city'
		  def working = [name: 'alice', locations: [[street: 'street1', city: sharedCity]]]
		  def base = [name: 'alice', locations: [[street: 'street2', city: sharedCity]]]

		when:
		  def locationPath = NodePath.startBuilding().mapKey('locations').build()
		  def locationIdentityStrategy = new LocationIdentityStrategy()
		  def node = ObjectDifferBuilder.startBuilding()
				  .identity()
				  .ofCollectionItems(locationPath).via(locationIdentityStrategy)
				  .and().build()
				  .compare(working, base);

		then:
		  def leafNodeCountingVisitor = new LeafNodeCountingVisitor()
		  node.visit(leafNodeCountingVisitor)
		  leafNodeCountingVisitor.leafNodeCount == 1

		and:
		  def streetNode = node.getChild([
				  new MapKeyElementSelector('locations'),
				  new CollectionItemElementSelector([city: sharedCity]),
				  new MapKeyElementSelector('street')
		  ])
		  streetNode.state == DiffNode.State.CHANGED
	}
}