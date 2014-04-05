/*
 * Copyright 2013 Daniel Bechler
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

import de.danielbechler.diff.bean.BeanPropertyElementSelector
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
@SuppressWarnings("GroovyPointlessBoolean")
class InclusionServiceSpec extends Specification {
	def categoryResolver = Mock(CategoryResolver)
	def accessor = Mock(PropertyAwareAccessor)
	def inclusionService = new InclusionService(categoryResolver)
	def NodePath nodePath = NodePath.with("foo")
	def DiffNode rootNode
	def DiffNode node

	def "setup"() {
		accessor.elementSelector >> new BeanPropertyElementSelector("foo")
		rootNode = new DiffNode(RootAccessor.instance, null)
		node = new DiffNode(rootNode, accessor, null)
		categoryResolver.resolveCategories(_ as DiffNode) >> []
	}

	def "construction: should fail if no categoryResolver is given"() {
		when:
		  new InclusionService(null)

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
		  inclusionService.toInclude().categories("unknown-category")

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'false' if node doesn't match defined inclusion rules but is root node"() {
		given:
		  inclusionService.toInclude().categories("unknown-category")

		expect:
		  !inclusionService.isIgnored(rootNode)
	}

	def "isIgnored: should return 'false' if node is included via path"() {
		setup:
		  inclusionService.toInclude().node(nodePath)

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'false' if node is included via category"() {
		given:
		  categoryResolver.resolveCategories(node) >> ["test-category"]

		when:
		  def ignored = inclusionService.isIgnored(node)

		then:
		  inclusionService.toInclude().categories("test-category")

		and:
		  ignored == false
	}

	def "isIgnored: should return 'false' if node is included via type"() {
		setup:
		  node.setType(URL)
		  inclusionService.toInclude().types(URL)

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'false' if node doesn't match exclusion rules"() {
		setup:
		  inclusionService.toExclude().categories("unknown-category")

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'true' if node is excluded via path"() {
		setup:
		  inclusionService.toExclude().node(nodePath)

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'true' if node is excluded via type"() {
		setup:
		  node.setType(URL)
		  inclusionService.toExclude().types(URL)

		expect:
		  inclusionService.isIgnored(node)
	}

	def "isIgnored: should return 'true' if node is excluded via category"() {
		given:
		  inclusionService.toExclude().categories("test-category")

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
		  inclusionService.toExclude().propertyNames(propertyName)

		expect:
		  inclusionService.isIgnored(node) == true
	}

	def "isIgnored: should return 'false' when node is included via property name"() {
		given:
		  def propertyName = "foo"
		  def propertyAwareAccessor = mockPropertyAwareAccessor(propertyName)
		  def node = new DiffNode(propertyAwareAccessor, null)

		and:
		  inclusionService.toInclude().propertyNames(propertyName)

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
		  inclusionService.toInclude().node(NodePath.withRoot())

		expect:
		  inclusionService.isIgnored(node) == false
	}

	def "isIgnored: should return 'true' for children of excluded nodes"() {
		given:
		  inclusionService.toExclude().node(NodePath.withRoot())

		expect:
		  inclusionService.isIgnored(node) == true
	}
}
