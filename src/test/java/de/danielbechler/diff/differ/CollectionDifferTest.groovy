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

package de.danielbechler.diff.differ

import de.danielbechler.diff.access.CollectionItemAccessor
import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.comparison.ComparisonStrategy
import de.danielbechler.diff.comparison.ComparisonStrategyResolver
import de.danielbechler.diff.identity.EqualsIdentityStrategy
import de.danielbechler.diff.identity.IdentityStrategyResolver
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

public class CollectionDifferTest extends Specification {

	ComparisonStrategyResolver comparisonStrategyResolver = Mock()
	ComparisonStrategy comparisonStrategy = Mock()
	DifferDispatcher differDispatcher = Mock()
	Instances instances = Mock()
	IdentityStrategyResolver identityStrategyResolver = Stub()

	CollectionDiffer collectionDiffer
	DiffNode node
	Collection<String> baseCollection
	Collection<String> workingCollection

	def setup() {
		collectionDiffer = new CollectionDiffer(differDispatcher, comparisonStrategyResolver, identityStrategyResolver)
		baseCollection = new HashSet<String>()
		workingCollection = new HashSet<String>()
		instances = Stub(Instances) {
			getSourceAccessor() >> RootAccessor.getInstance()
			getType() >> List
			getBase(Collection) >> baseCollection
			getWorking(Collection) >> workingCollection
		}
	}

	def 'accepts all collection types'() {
		expect:
		  collectionDiffer.accepts(type) == true
		where:
		  type << [Collection, List, Queue, Set, ArrayList, LinkedList]
	}

	def 'rejects non-collection types'() {
		expect:
		  collectionDiffer.accepts(type) == false
		where:
		  type << [Object, ObjectWithString, Date]
	}

	def 'fails instantiation'() {
		when:
		  new CollectionDiffer(differDispatcher, comparisonStrategyResolver, identityStrategyResolver)
		then:
		  thrown(IllegalArgumentException)
		where:
		  differDispatcher       | comparisonStrategyResolver       | identityStrategyResolver
		  null                   | Mock(ComparisonStrategyResolver) | Mock(IdentityStrategyResolver)
		  Mock(DifferDispatcher) | null                             | Mock(IdentityStrategyResolver)
		  Mock(DifferDispatcher) | Mock(ComparisonStrategyResolver) | null
	}

	def 'returns untouched node when instances are same'() {
		given:
		  instances.areSame() >> true
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances)
		then:
		  node.state == DiffNode.State.UNTOUCHED
	}

	def 'returns added node when instance has been added'() {
		given:
		  instances.hasBeenAdded() >> true
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances)
		then:
		  node.state == DiffNode.State.ADDED
	}

	def 'returns removed node when instance has been removed'() {
		given:
		  instances.hasBeenRemoved() >> true
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances);
		then:
		  node.state == DiffNode.State.REMOVED
	}

	def 'delegates added items to dispatcher when instance has been added'() {
		given:
		  instances = Mock(Instances) {
			  getSourceAccessor() >> RootAccessor.instance
			  hasBeenAdded() >> true
			  getWorking(Collection) >> ["foo"]
		  }
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances)
		then:
		  1 * differDispatcher.dispatch(_, instances, _) >> { parentNode, instances, accessor ->
			  assert parentNode != null
			  assert accessor instanceof CollectionItemAccessor
		  }
		and:
		  0 * differDispatcher.dispatch(*_)
	}

	def 'delegate removed items to dispatcher when instance has been removed'() {
		given:
		  instances = Mock(Instances) {
			  getSourceAccessor() >> RootAccessor.instance
			  hasBeenRemoved() >> true
			  getBase(Collection) >> ["foo"]
		  }
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances);
		then:
		  1 * differDispatcher.dispatch(_, instances, _) >> { parentNode, instances, accessor ->
			  assert parentNode != null
			  assert accessor instanceof CollectionItemAccessor
		  }
		and:
		  0 * differDispatcher.dispatch(*_)
	}

	def 'compare using comparison strategy if available'() {
		given:
		  comparisonStrategyResolver.resolveComparisonStrategy(_) >> comparisonStrategy
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances);
		then:
		  1 * comparisonStrategy.compare(_, _, workingCollection, baseCollection) >> { node, type, working, base ->
			  assert node.path.matches(NodePath.withRoot())
			  assert type == List
		  }
	}

	def 'delegate items to dispatcher when performing deep comparison'() {
		given:
		  instances = Mock(Instances) {
			  getSourceAccessor() >> RootAccessor.instance
			  getWorking(Collection) >> working
			  getBase(Collection) >> base
		  }
		and:
		  identityStrategyResolver.resolveIdentityStrategy(_) >> {
			  return EqualsIdentityStrategy.instance
		  }
		when:
		  node = collectionDiffer.compare(DiffNode.ROOT, instances);
		then:
		  1 * differDispatcher.dispatch(_, instances, _) >> { parentNode, instances, accessor ->
			  assert parentNode.path.matches(NodePath.withRoot())
			  assert accessor instanceof CollectionItemAccessor
		  }
		and:
		  0 * differDispatcher.dispatch(*_)
		where:
		  working   | base
		  ['added'] | []
		  ['known'] | ['known']
		  []        | ['removed']
	}
}
