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

import de.danielbechler.diff.category.CategoryConfigurer
import de.danielbechler.diff.circular.CircularReferenceConfigurer
import de.danielbechler.diff.comparison.ComparisonConfigurer
import de.danielbechler.diff.differ.Differ
import de.danielbechler.diff.differ.DifferConfigurer
import de.danielbechler.diff.differ.DifferFactory
import de.danielbechler.diff.filtering.FilteringConfigurer
import de.danielbechler.diff.identity.IdentityConfigurer
import de.danielbechler.diff.inclusion.InclusionConfigurer
import de.danielbechler.diff.introspection.IntrospectionConfigurer
import spock.lang.Specification

class ObjectDifferBuilderTest extends Specification {

	def 'startBuilding returns ObjectDifferBuilder instance'() {
		expect:
		  ObjectDifferBuilder.startBuilding() instanceof ObjectDifferBuilder
	}

	def 'startBuilding always returns a new ObjectDifferBuilder instance'() {
		expect:
		  !ObjectDifferBuilder.startBuilding().is(ObjectDifferBuilder.startBuilding())
	}

	def 'buildDefault returns ObjectDiffer instance'() {
		expect:
		  ObjectDifferBuilder.buildDefault() instanceof ObjectDiffer
	}

	def 'buildDefault always returns a new ObjectDiffer instance'() {
		expect:
		  !ObjectDifferBuilder.buildDefault().is(ObjectDifferBuilder.buildDefault())
	}

	def 'categories returns CategoryConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().categories() instanceof CategoryConfigurer
	}

	def 'circularReferenceHandling returns CircularReferenceConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().circularReferenceHandling() instanceof CircularReferenceConfigurer
	}

	def 'comparison returns ComparisonConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().comparison() instanceof ComparisonConfigurer
	}

	def 'differs returns DifferConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().differs() instanceof DifferConfigurer
	}

	def 'filtering returns FilteringConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().filtering() instanceof FilteringConfigurer
	}

	def 'identity returns IdentityConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().identity() instanceof IdentityConfigurer
	}

	def 'inclusion returns InclusionConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().inclusion() instanceof InclusionConfigurer
	}

	def 'introspection returns IntrospectionConfigurer'() {
		expect:
		  ObjectDifferBuilder.startBuilding().introspection() instanceof IntrospectionConfigurer
	}

	def 'build returns new ObjectDiffer instance'() {
		expect:
		  ObjectDifferBuilder.startBuilding().build() instanceof ObjectDiffer
	}

	def 'build invokes all registered DifferFactories'() {
		def builder = ObjectDifferBuilder.startBuilding()
		def differFactory = Mock(DifferFactory)
		given:
		  builder.differs().register(differFactory)
		when:
		  builder.build()
		then:
		  1 * differFactory.createDiffer(_, _) >> Stub(Differ)
	}
}
