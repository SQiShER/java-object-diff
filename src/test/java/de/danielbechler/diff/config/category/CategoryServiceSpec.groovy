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

package de.danielbechler.diff.config.category

import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class CategoryServiceSpec extends Specification {
	def categoryService = new CategoryService()
	def accessor = Mock(PropertyAwareAccessor)
	def nodePath = NodePath.with("foo")
	def nodeType = Alliance
	def DiffNode node
	def DiffNode rootNode

	def "setup"() {
		accessor.elementSelector >> new BeanPropertyElementSelector("foo")
		rootNode = new DiffNode(DiffNode.ROOT, RootAccessor.instance, null)
		node = new DiffNode(rootNode, accessor, nodeType)
	}

	def "resolveCategories: should return categories configured via path"() {
		given:
		  categoryService.ofNode(nodePath).toBe("Stark", "Lannister")

		expect:
		  categoryService.resolveCategories(node) == ["Stark", "Lannister"] as Set
	}

	def "resolveCategories: should return categories configured via type"() {
		given:
		  categoryService.ofType(nodeType).toBe("Stark", "Lannister")

		expect:
		  categoryService.resolveCategories(node) == ["Stark", "Lannister"] as Set
	}

	def "resolveCategories: should return categories configured via node"() {
		given:
		  accessor.categories >> ["Stark", "Lannister"]

		expect:
		  categoryService.resolveCategories(node) == ["Stark", "Lannister"] as Set
	}

	def "resolveCategories: should return combined categories configured via all possible options"() {
		given:
		  categoryService.ofNode(nodePath).toBe("A")

		and:
		  categoryService.ofType(nodeType).toBe("B")

		and:
		  accessor.categories >> ["C"]

		expect:
		  categoryService.resolveCategories(node) == ["A", "B", "C"] as Set
	}

	def "resolveCategories: should also return categories of parent nodes"() {
		given:
		  accessor.categories >> ["B"]

		and:
		  categoryService.ofNode(NodePath.withRoot()).toBe("A")

		expect:
		  categoryService.resolveCategories(node) == ["A", "B"] as Set
	}

	def "resolveCategories: should return empty Set if no category is defined"() {
		given:
		  node = new DiffNode()

		expect:
		  categoryService.resolveCategories(node) == [] as Set
	}

	class Alliance {
	}
}
