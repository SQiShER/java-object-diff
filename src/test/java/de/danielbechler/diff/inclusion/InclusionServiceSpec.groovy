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

package de.danielbechler.diff.inclusion

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.category.CategoryResolver
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class InclusionServiceSpec extends Specification {
	def categoryResolver = Mock(CategoryResolver)
	def accessor = Mock(PropertyAwareAccessor)
	def builder = Mock(ObjectDifferBuilder)
	def inclusionService = new InclusionService(categoryResolver, builder)
	def NodePath nodePath = NodePath.with("foo")
	def DiffNode rootNode
	def DiffNode node

	def "setup"() {
		accessor.elementSelector >> new BeanPropertyElementSelector("foo")
		rootNode = new DiffNode(RootAccessor.instance, null)
		node = new DiffNode(rootNode, accessor, null)
		categoryResolver.resolveCategories(_ as DiffNode) >> []
	}

	def "construction: should fail when no categoryResolver is given"() {
		when:
		  new InclusionService(null, builder)

		then:
		  thrown(IllegalArgumentException)
	}

	def "construction: should fail when no root configuration is given"() {
		when:
		  new InclusionService(categoryResolver, null)

		then:
		  thrown(IllegalArgumentException)
	}

	def "isIgnored: should return 'true' if node is marked as ignored"() {
		given:
		  accessor.isExcluded() >> true

		expect:
		  inclusionService.isIgnored(node)
	}

	def "isIgnored: should return 'false' if no include and exclude rules are defined"() {
		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'true' if node doesn't match defined inclusion rules"() {
		setup:
		  inclusionService.include().category("unknown-category")

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'false' if node doesn't match defined inclusion rules but is root node"() {
		given:
		  inclusionService.include().category("unknown-category")

		expect:
		  !inclusionService.isIgnored(rootNode)
	}

	def "isIgnored: should return 'false' if node is included via path"() {
		setup:
		  inclusionService.include().node(nodePath)

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'false' if node is included via category"() {
		given:
		  categoryResolver.resolveCategories(node) >> ["test-category"]

		when:
		  def ignored = inclusionService.isIgnored(node)

		then:
		  inclusionService.include().category("test-category")

		and:
		  ignored == false
	}

	def "isIgnored: should return 'false' if node is included via type"() {
		setup:
		  node.setType(URL)
		  inclusionService.include().type(URL)

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'false' if node doesn't match exclusion rules"() {
		setup:
		  inclusionService.exclude().category("unknown-category")

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'true' if node is excluded via path"() {
		setup:
		  inclusionService.exclude().node(nodePath)

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'true' if node is excluded via type"() {
		setup:
		  node.setType(URL)
		  inclusionService.exclude().type(URL)

		expect:
		  inclusionService.isIgnored(node)
	}

	def "isIgnored: should return 'true' if node is excluded via category"() {
		given:
		  inclusionService.exclude().category("test-category")

		when:
		  def ignored = inclusionService.isIgnored(node)

		then:
		  categoryResolver.resolveCategories(node) >> ["test-category"]

		and:
		  ignored == true
	}

	def "isIgnored: should return 'true' when node is excluded via property name"() {
		given:
		  def propertyName = "foo"
		  def propertyAwareAccessor = mockPropertyAwareAccessor(propertyName)
		  def node = new DiffNode(propertyAwareAccessor, null)

		and:
		  inclusionService.exclude().propertyName(propertyName)

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'false' when node is included via property name"() {
		given:
		  def propertyName = "foo"
		  def propertyAwareAccessor = mockPropertyAwareAccessor(propertyName)
		  def node = new DiffNode(propertyAwareAccessor, null)

		and:
		  inclusionService.include().propertyName(propertyName)

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def mockPropertyAwareAccessor(String name) {
		def propertyAwareAccessor = Mock(PropertyAwareAccessor)
		propertyAwareAccessor.elementSelector >> new BeanPropertyElementSelector(name)
		propertyAwareAccessor.propertyName >> name
		propertyAwareAccessor
	}

	def "isIgnored: should return 'false' for children of included nodes"() {
		given:
		  inclusionService.include().node(NodePath.withRoot())

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'true' for children of excluded nodes"() {
		given:
		  inclusionService.exclude().node(NodePath.withRoot())

		expect:
		  inclusionService.isIgnored(node) == true
	}
}
