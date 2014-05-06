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

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.path.NodePath
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class IntrospectionAT extends Specification {

	ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding()

	Introspector introspector = Mock(Introspector)

	@Shared
	Role base = new Role(name: 'Daario Naharis', actor: new Actor(firstName: 'Ed', lastName: 'Skrein'))
	@Shared
	Role working = new Role(name: 'Daario Naharis', actor: new Actor(firstName: 'Michiel', lastName: 'Huisman'))

	def 'set default introspector'() {
		given:
		  objectDifferBuilder.introspection().setDefaultIntrospector(introspector)
		when:
		  objectDifferBuilder.build().compare(working, base)
		then:
		  1 * introspector.introspect(Role) >> []
	}

	def 'disable introspection for node'() {
		given:
		  objectDifferBuilder.introspection().ofNode(NodePath.with('actor')).toBeDisabled()
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.isUntouched()
	}

	def 'disable introspection for type'() {
		given:
		  objectDifferBuilder.introspection().ofType(Actor).toBeDisabled()
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.isUntouched()
	}

	def 'enable introspection for node'() {
		given:
		  objectDifferBuilder.introspection().ofNode(NodePath.with('actor')).toBeDisabled()
		and:
		  objectDifferBuilder.introspection().ofNode(NodePath.with('actor')).toBeEnabled()
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('actor').childCount() == 2
	}

	def 'enable introspection for type'() {
		given:
		  objectDifferBuilder.introspection().ofType(Actor).toBeDisabled()
		and:
		  objectDifferBuilder.introspection().ofType(Actor).toBeEnabled()
		when:
		  def node = objectDifferBuilder.build().compare(working, base)
		then:
		  node.getChild('actor').childCount() == 2
	}

	def 'configure custom introspector for node'() {
		given:
		  objectDifferBuilder.introspection().ofNode(NodePath.with('actor')).toUse(introspector)
		when:
		  objectDifferBuilder.build().compare(working, base)
		then:
		  0 * introspector.introspect(Role)
		and:
		  1 * introspector.introspect(Actor) >> []
	}

	def 'configure custom introspector for type'() {
		given:
		  objectDifferBuilder.introspection().ofType(Actor).toUse(introspector)
		when:
		  objectDifferBuilder.build().compare(working, base)
		then:
		  0 * introspector.introspect(Role)
		and:
		  1 * introspector.introspect(Actor) >> []
	}

	@ToString
	@EqualsAndHashCode(includes = 'name')
	static class Role {
		String name
		Actor actor
	}

	@ToString
	@EqualsAndHashCode(includes = ['firstName', 'lastName'])
	static class Actor {
		String firstName
		String lastName
	}

}
