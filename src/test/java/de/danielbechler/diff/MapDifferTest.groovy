/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff

import de.danielbechler.diff.config.comparison.ComparisonStrategyResolver
import de.danielbechler.diff.config.filtering.IsReturnableResolver
import de.danielbechler.diff.config.comparison.ComparisonStrategy
import de.danielbechler.diff.node.Accessor
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.MapEntryAccessor
import de.danielbechler.diff.node.RootAccessor
import spock.lang.Specification

import static de.danielbechler.diff.node.DiffNode.State.*

/** @author Daniel Bechler                */
public class MapDifferTest extends Specification {

	def MapDiffer mapDiffer
	def node
	def working = [] as HashMap
	def base = [] as HashMap

	def childNode = Mock(DiffNode)
	def instances = Mock(Instances)
	def differDispatcher = Mock(DifferDispatcher)
	def comparisonStrategyResolver = Mock(ComparisonStrategyResolver)
	def isReturnableResolver = Mock(IsReturnableResolver)
	def comparisonStrategy = Mock(ComparisonStrategy)

	def setup() {
		differDispatcher.dispatch(_ as DiffNode, _ as Instances, _ as Accessor) >> childNode
		mapDiffer = new MapDiffer(differDispatcher, comparisonStrategyResolver)
		instances.sourceAccessor >> RootAccessor.instance
		instances.getWorking(Map) >> working
		instances.getBase(Map) >> base
		isReturnableResolver.isReturnable(_ as DiffNode) >> true
	}

	def "mark node as added when map was null and has been changed to an instance"() {
		setup:
		  instances.hasBeenAdded() >> true

		expect:
		  mapDiffer.compare(DiffNode.ROOT, instances).state == ADDED
	}

	def "dispatch comparison of each entry of an added map"() {
		given:
		  working.put('1', 'one')
		  working.put('2', 'two')
		  instances.hasBeenAdded() >> true

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, _ as Instances, new MapEntryAccessor('1'))
		  1 * differDispatcher.dispatch(_ as DiffNode, _ as Instances, new MapEntryAccessor('2'))
	}

	def "mark node as removed when map was an instance and has been changed to null"() {
		setup:
		  instances.hasBeenRemoved() >> true

		expect:
		  mapDiffer.compare(DiffNode.ROOT, instances).state == REMOVED
	}

	def "mark node as untouched when map instances are identical"() {
		setup:
		  instances.areSame() >> true

		expect:
		  mapDiffer.compare(DiffNode.ROOT, instances).state == UNTOUCHED
	}

	def "do not compare entries of identical map instances"() {
		given:
		  instances.areSame() >> true

		and:
		  working.put('foo', 'bar')

		when:
		  mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  0 * differDispatcher._
	}

	def "dispatch comparison of added map entry"() {
		given:
		  working.put("foo", "bar")

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, instances, new MapEntryAccessor('foo')) >> childNode
	}

	def "dispatch comparison of removed map entry"() {
		given:
		  base.put("foo", "bar")

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, instances, new MapEntryAccessor('foo')) >> childNode
	}

	def "delegate to comparison strategy returned by ComparisonStrategyResolver"() {
		given:
		  comparisonStrategyResolver.resolveComparisonStrategy(_ as DiffNode) >> comparisonStrategy

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  1 * comparisonStrategy.compare(_ as DiffNode, instances.getType(), instances.getWorking(Map), instances.getBase(Map))
	}

	def "do not add unreturnable child nodes"() {
		given:
		  working.put("foo", "bar")

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  isReturnableResolver.isReturnable(childNode) >> false

		and:
		  node.hasChildren() == false
	}

	def "dispatch comparison of each entry of a removed map"() {
		base.put('1', 'one')
		base.put('2', 'two')
		instances.hasBeenRemoved() >> true

		when:
		  node = mapDiffer.compare(DiffNode.ROOT, instances)

		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, _ as Instances, new MapEntryAccessor('1'))
		  1 * differDispatcher.dispatch(_ as DiffNode, _ as Instances, new MapEntryAccessor('2'))
	}

	def "fail when constructed without delegator"() {
		when:
		  new MapDiffer(null, comparisonStrategyResolver);

		then:
		  thrown(IllegalArgumentException)
	}

	def "return node with proper parent"() {
		given:
		  def DiffNode parentNode = new DiffNode()

		when:
		  node = mapDiffer.compare(parentNode, instances)

		then:
		  node.parentNode == parentNode
	}

	def "accepts all implementations of java.util.Map"(Class<?> type, def accept) {
		expect:
		  mapDiffer.accepts(type) == accept

		where:
		  type       || accept
		  Map        || true
		  HashMap    || true
		  TreeMap    || true
		  LinkedList || false
		  String     || false
		  null       || false
	}
}
