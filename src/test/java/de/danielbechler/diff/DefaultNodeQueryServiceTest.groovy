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

package de.danielbechler.diff

import de.danielbechler.diff.category.CategoryResolver
import de.danielbechler.diff.comparison.ComparisonStrategy
import de.danielbechler.diff.comparison.ComparisonStrategyResolver
import de.danielbechler.diff.comparison.PrimitiveDefaultValueMode
import de.danielbechler.diff.comparison.PrimitiveDefaultValueModeResolver
import de.danielbechler.diff.filtering.IsReturnableResolver
import de.danielbechler.diff.inclusion.IsIgnoredResolver
import de.danielbechler.diff.introspection.IsIntrospectableResolver
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Unroll

class DefaultNodeQueryServiceTest extends Specification {

	DefaultNodeQueryService nodeQueryService

	def node = DiffNode.newRootNode()
	def categoryResolver = Mock(CategoryResolver)
	def introspectableResolver = Mock(IsIntrospectableResolver)
	def ignoredResolver = Mock(IsIgnoredResolver)
	def returnableResolver = Mock(IsReturnableResolver)
	def comparisonStrategyResolver = Mock(ComparisonStrategyResolver)
	def primitiveDefaultValueModeResolver = Mock(PrimitiveDefaultValueModeResolver)

	def setup() {
		nodeQueryService = new DefaultNodeQueryService(
				categoryResolver,
				introspectableResolver,
				ignoredResolver,
				returnableResolver,
				comparisonStrategyResolver,
				primitiveDefaultValueModeResolver)
	}

	def "resolveCategories delegates to CategoryResolver"() {
		def categories = ['foo']
		setup:
		  1 * categoryResolver.resolveCategories(node) >> categories
		expect:
		  with(nodeQueryService.resolveCategories(node)) {
			  assert it.containsAll(categories)
			  assert it.size() == categories.size()
		  }
	}

	@Unroll
	def "isIntrospectable delegates to IsIntrospectableResolver (#introspectable)"() {
		setup:
		  1 * introspectableResolver.isIntrospectable(node) >> introspectable
		expect:
		  nodeQueryService.isIntrospectable(node) == introspectable
		where:
		  introspectable << [true, false]
	}

	@Unroll
	def "isIgnored delegates to IsIgnoredResolver (#ignored)"() {
		setup:
		  1 * ignoredResolver.isIgnored(node) >> ignored
		expect:
		  nodeQueryService.isIgnored(node) == ignored
		where:
		  ignored << [true, false]
	}

	@Unroll
	def "isReturnable delegates to IsReturnableResolver (#returnable)"() {
		setup:
		  1 * returnableResolver.isReturnable(node) >> returnable
		expect:
		  nodeQueryService.isReturnable(node) == returnable
		where:
		  returnable << [true, false]
	}

	def "resolveComparisonStrategy delegates to ComparisonStrategyResolver"() {
		def comparisonStrategy = Mock(ComparisonStrategy)
		setup:
		  1 * comparisonStrategyResolver.resolveComparisonStrategy(node) >> comparisonStrategy
		expect:
		  nodeQueryService.resolveComparisonStrategy(node) is(comparisonStrategy)
	}

	@Unroll
	def "resolvePrimitiveDefaultValueMode delegates to PrimitiveDefaultValueModeResolver (#valueMode)"() {
		setup:
		  1 * primitiveDefaultValueModeResolver.resolvePrimitiveDefaultValueMode(node) >> valueMode
		expect:
		  nodeQueryService.resolvePrimitiveDefaultValueMode(node) == valueMode
		where:
		  valueMode << PrimitiveDefaultValueMode.values()
	}
}
