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

package de.danielbechler.diff.introspection

import de.danielbechler.diff.access.RootAccessor
import de.danielbechler.diff.access.TypeAwareAccessor
import de.danielbechler.diff.circular.CircularReferenceMatchingMode
import de.danielbechler.diff.inclusion.Inclusion
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Daniel Bechler
 */
class IntrospectionServiceTest extends Specification {
	@Shared
	def primitiveTypes = [null, int, short, long, boolean, char, byte, float, double, float]

	@Shared
	def primitiveWrapperTypes = [Integer, Short, Long, Boolean, Character, Byte, Double, Float]

	def introspectionService = new IntrospectionService()
	def rootNode = new DiffNode(DiffNode.ROOT, RootAccessor.instance, ObjectWithString)
	def childNode
	def childAccessor = Mock(TypeAwareAccessor)
	def defaultIntrospector = Mock(Introspector)

	def "setup"() {
		introspectionService.setDefaultIntrospector(defaultIntrospector)
	}

	@Unroll
	def 'introspection should always be disabled for #type'() {
		given:
		  childAccessor.getType() >> type
		  childNode = new DiffNode(rootNode, childAccessor, type)
		  introspectionService.ofType(type).toBeEnabled()

		expect:
		  !introspectionService.isIntrospectable(childNode)

		where:
		  type << primitiveTypes + primitiveWrapperTypes
	}

	def 'introspection should always be disabled for Arrays'() {
		given:
		  rootNode = new DiffNode(DiffNode.ROOT, RootAccessor.instance, type);
		  introspectionService.ofType(type).toBeEnabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)

		  //noinspection GroovyAssignabilityCheck
		where:
		  type << [int[], String[], Object[]]
	}

	def 'introspection should always be disabled for Enums'() {
		given:
		  rootNode = new DiffNode(DiffNode.ROOT, RootAccessor.instance, type);
		  introspectionService.ofType(type).toBeEnabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)

		where:
		  type << [Inclusion, CircularReferenceMatchingMode]
	}

	def 'introspection should always be disabled for nodes with unknown type (null)'() {
		given:
		  rootNode = new DiffNode(DiffNode.ROOT, RootAccessor.instance, null);
		  introspectionService.ofType(null).toBeEnabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be enabled via type'() {
		given:
		  introspectionService.ofType(ObjectWithString).toBeEnabled()

		expect:
		  introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be disabled via type'() {
		given:
		  introspectionService.ofType(ObjectWithString).toBeDisabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be re-enabled via type'() {
		given:
		  introspectionService.ofType(ObjectWithString).toBeDisabled()
		  introspectionService.ofType(ObjectWithString).toBeEnabled()

		expect:
		  introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be enabled via node'() {
		given:
		  introspectionService.ofNode(rootNode.path).toBeEnabled()

		expect:
		  introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be disabled via node'() {
		given:
		  introspectionService.ofNode(rootNode.path).toBeDisabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)
	}

	def 'introspection can be re-enabled via node'() {
		given:
		  introspectionService.ofNode(rootNode.path).toBeDisabled()
		  introspectionService.ofNode(rootNode.path).toBeEnabled()

		expect:
		  introspectionService.isIntrospectable(rootNode)
	}

	def 'node configuration overrules type configuration'() {
		given:
		  introspectionService.ofNode(rootNode.path).toBeDisabled()
		  introspectionService.ofType(ObjectWithString).toBeEnabled()

		expect:
		  !introspectionService.isIntrospectable(rootNode)
	}

	def 'attempting to set default introspector to null should cause IllegalArgumentException'() {
		when:
		  introspectionService.setDefaultIntrospector(null)

		then:
		  thrown(IllegalArgumentException)
	}

	def 'introspectorForNode returns default introspector if no special configuration exists'() {
		given:
		  def introspector = Mock(Introspector)
		  introspectionService.setDefaultIntrospector(introspector)

		expect:
		  introspectionService.introspectorForNode(rootNode) == introspector
	}

	def 'introspectorForNode returns introspector configured via type'() {
		given:
		  def typeIntrospector = Mock(Introspector)
		  introspectionService.ofType(String).toUse(typeIntrospector)
		  rootNode = new DiffNode(String)

		expect:
		  introspectionService.introspectorForNode(rootNode) == typeIntrospector
	}

	def 'introspectorForNode returns introspector configured via node path'() {
		given:
		  def nodeIntrospector = Mock(Introspector)
		  introspectionService.ofNode(NodePath.withRoot()).toUse(nodeIntrospector)

		expect:
		  introspectionService.introspectorForNode(rootNode) == nodeIntrospector
	}
}
