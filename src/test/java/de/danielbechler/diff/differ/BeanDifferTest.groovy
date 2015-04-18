/*
 * Copyright 2015 Daniel Bechler
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
import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.comparison.ComparisonStrategy
import de.danielbechler.diff.comparison.ComparisonStrategyResolver
import de.danielbechler.diff.filtering.IsReturnableResolver
import de.danielbechler.diff.instantiation.TypeInfo
import de.danielbechler.diff.introspection.IsIntrospectableResolver
import de.danielbechler.diff.introspection.TypeInfoResolver
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.FailsWith
import spock.lang.Specification
import spock.lang.Unroll

class BeanDifferTest extends Specification {

	DifferDispatcher differDispatcher = Mock(DifferDispatcher)
	Instances instances = Mock(Instances) {
		getBase() >> 'any'
		getWorking() >> 'any'
		getFresh() >> 'any'
		getSourceAccessor() >> RootAccessor.instance
	}
	IsIntrospectableResolver introspectableResolver = Mock(IsIntrospectableResolver)
	IsReturnableResolver returnableResolver = Mock(IsReturnableResolver)
	ComparisonStrategyResolver comparisonStrategyResolver = Mock(ComparisonStrategyResolver)
	ComparisonStrategy comparisonStrategy = Mock(ComparisonStrategy)
	TypeInfoResolver typeInfoResolver = Mock(TypeInfoResolver)

	BeanDiffer beanDiffer = new BeanDiffer(
			differDispatcher,
			introspectableResolver,
			returnableResolver,
			comparisonStrategyResolver,
			typeInfoResolver)

	@Unroll
	def 'accepts all object types (e.g. #type)'() {
		expect:
		  beanDiffer.accepts(type)
		where:
		  type << [Object, Number, Number, Date]
	}

	@Unroll
	def 'rejects all primitive types (e.g. #type)'() {
		expect:
		  beanDiffer.accepts(type) == false
		where:
		  type << [int, int[], boolean]
	}

	def 'returns untouched node if working and base are null'() {
		given:
		  instances.areNull() >> true
		expect:
		  beanDiffer.compare(DiffNode.ROOT, instances).isUntouched()
	}

	def 'returns added node if working is not null and base is'() {
		given:
		  instances.hasBeenAdded() >> true
		expect:
		  beanDiffer.compare(DiffNode.ROOT, instances).isAdded()
	}

	def 'returns removed node if working is null and base is not'() {
		given:
		  instances.hasBeenRemoved() >> true
		expect:
		  beanDiffer.compare(DiffNode.ROOT, instances).isRemoved()
	}

	def 'returns untouched node if working and base are the same instance'() {
		given:
		  instances.areSame() >> true
		expect:
		  beanDiffer.compare(DiffNode.ROOT, instances).isUntouched()
	}

	def 'compares via comparisonStrategy if one exists'() {
		given:
		  comparisonStrategyResolver.resolveComparisonStrategy(_ as DiffNode) >> comparisonStrategy
		when:
		  beanDiffer.compare(DiffNode.ROOT, instances);
		then:
		  1 * comparisonStrategy.compare(_ as DiffNode, instances.type, instances.working, instances.base)
	}

	def 'compares via introspection if object is introspectable'() {
		given:
		  introspectableResolver.isIntrospectable(_ as DiffNode) >> true
		when:
		  beanDiffer.compare(DiffNode.ROOT, instances)
		then:
		  1 * typeInfoResolver.typeInfoForNode(_ as DiffNode) >> new TypeInfo(Object)
	}

	def 'delegate comparison of properties to DifferDispatcher when comparing via introspector'() {
		given: 'the root object is introspectable'
		  introspectableResolver.isIntrospectable({ DiffNode node -> node.isRootNode() }) >> true
		and: 'the root object type info contains an accessor'
		  def accessor = Mock(PropertyAwareAccessor)
		  def typeInfo = new TypeInfo(Object)
		  typeInfo.addPropertyAccessor(accessor)
		  typeInfoResolver.typeInfoForNode(_ as DiffNode) >> typeInfo
		and: 'the property node returned by the dispatcher is returnable'
		  def propertyNode = Mock(DiffNode) {
			  getElementSelector() >> new BeanPropertyElementSelector('example')
		  }
		  returnableResolver.isReturnable(propertyNode) >> true
		when:
		  def rootNode = beanDiffer.compare(DiffNode.ROOT, instances)
		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, instances, accessor) >> propertyNode
		and:
		  rootNode.childCount() == 1
		  rootNode.getChild(propertyNode.elementSelector) == propertyNode
	}

	def 'add property nodes only if they are returnable'() {
		given: 'the root object is introspectable'
		  introspectableResolver.isIntrospectable({ DiffNode node -> node.isRootNode() }) >> true
		and: 'the root object type info contains an accessor'
		  def accessor = Mock(PropertyAwareAccessor)
		  def typeInfo = new TypeInfo(Object)
		  typeInfo.addPropertyAccessor(accessor)
		  typeInfoResolver.typeInfoForNode(_ as DiffNode) >> typeInfo
		and: 'the property node returned by the dispatcher is NOT returnable'
		  def propertyNode = Mock(DiffNode) {
			  getElementSelector() >> new BeanPropertyElementSelector('example')
		  }
		  returnableResolver.isReturnable(propertyNode) >> false
		when:
		  def node = beanDiffer.compare(DiffNode.ROOT, instances)
		then:
		  1 * differDispatcher.dispatch(_ as DiffNode, instances, accessor) >> propertyNode
		and:
		  !node.hasChildren()
	}

	def 'assigns type info resolved via type info resolver to bean node when comparing via introspection'() {
		given:
		  def propertyAccessor = Mock(PropertyAwareAccessor)
		  def typeInfo = new TypeInfo(Date)
		  typeInfo.addPropertyAccessor(propertyAccessor)
		and:
		  def propertyNode = Mock(DiffNode) {
			  getElementSelector() >> new BeanPropertyElementSelector('example')
		  }
		when:
		  def rootNode = beanDiffer.compare(DiffNode.ROOT, instances)
		then:
		  1 * introspectableResolver.isIntrospectable({ DiffNode node -> node.isRootNode() }) >> true
		and:
		  1 * typeInfoResolver.typeInfoForNode({ DiffNode node -> node.isRootNode() }) >> typeInfo
		and:
		  1 * differDispatcher.dispatch({ DiffNode node -> node.isRootNode() }, instances, propertyAccessor) >> propertyNode
		and:
		  1 * returnableResolver.isReturnable(propertyNode) >> false
		and:
		  rootNode.valueTypeInfo == typeInfo
	}

	@Unroll
	@FailsWith(IllegalArgumentException)
	def 'construction fails with IllegalArgumentException'() {
		expect:
		  new BeanDiffer(differDispatcher, introspectableResolver, returnableResolver, comparisonStrategyResolver, typeInfoResolver)
		where:
		  differDispatcher       | introspectableResolver         | returnableResolver         | comparisonStrategyResolver       | typeInfoResolver
		  null                   | Mock(IsIntrospectableResolver) | Mock(IsReturnableResolver) | Mock(ComparisonStrategyResolver) | Mock(TypeInfoResolver)
		  Mock(DifferDispatcher) | null                           | Mock(IsReturnableResolver) | Mock(ComparisonStrategyResolver) | Mock(TypeInfoResolver)
		  Mock(DifferDispatcher) | Mock(IsIntrospectableResolver) | null                       | Mock(ComparisonStrategyResolver) | Mock(TypeInfoResolver)
		  Mock(DifferDispatcher) | Mock(IsIntrospectableResolver) | Mock(IsReturnableResolver) | null                             | Mock(TypeInfoResolver)
		  Mock(DifferDispatcher) | Mock(IsIntrospectableResolver) | Mock(IsReturnableResolver) | Mock(ComparisonStrategyResolver) | null
	}
}
