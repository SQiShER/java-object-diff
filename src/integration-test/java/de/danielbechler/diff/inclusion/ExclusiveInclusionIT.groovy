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
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification

class ExclusiveInclusionIT extends Specification {

	def objectDifferBuilder = ObjectDifferBuilder.startBuilding()
			.filtering()
			.returnNodesWithState(DiffNode.State.IGNORED).and()

	def 'inclusion via type based property does NOT exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'base'), c: new C(baz: 'working'))

		when:
		  def node = objectDifferBuilder
				  .inclusion()
				  .include().propertyNameOfType(B, 'foo').and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isIgnored()
		  node.getChild('c').getChild('baz').isChanged()
	}

	class A {
		B b
		C c
	}

	class B {
		String foo
		String bar
	}

	class C {
		String baz
	}

	def 'inclusion via property name does exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'working'), c: new C(baz: 'working'))

		when:
		  def node = objectDifferBuilder
				  .inclusion()
				  .include().propertyName('b').and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').hasChanges()
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isChanged()
		  node.getChild('c').isIgnored()
	}

	def 'inclusion via node path does exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'working'), c: new C(baz: 'working'))

		when:
		  def node = objectDifferBuilder
				  .inclusion()
				  .include().node(NodePath.with('b')).and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').hasChanges()
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isChanged()
		  node.getChild('c').isIgnored()
	}

	def 'inclusion via type does exclude nodes that are not explicitly included - even its children'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'working'), c: new C(baz: 'working'))

		when: 'the property with type B is explicitly included'
		  def node = objectDifferBuilder
				  .inclusion()
				  .include().type(B).and()
				  .build()
				  .compare(working, base)

		then: 'the included type should not be ignored'
		  node.getChild('b').isIgnored() == false

		and: 'its sibling with a different type schould be ignored'
		  node.getChild('c').isIgnored()

		and: 'the children are also ignored, even though the parent is included'
		  node.getChild('b').getChild('foo').isIgnored()
		  node.getChild('b').getChild('bar').isIgnored()
	}

	def 'inclusion via category does exclude nodes that are not explicitly included'() {
		given:
		  def base = new A(b: new B(foo: 'base', bar: 'base'), c: new C(baz: 'base'))
		  def working = new A(b: new B(foo: 'working', bar: 'working'), c: new C(baz: 'working'))

		when:
		  def node = objectDifferBuilder
				  .categories().ofNode(NodePath.with('b')).toBe('included-category').and()
				  .inclusion()
				  .include().category('included-category').and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('b').hasChanges()
		  node.getChild('b').getChild('foo').isChanged()
		  node.getChild('b').getChild('bar').isChanged()
		  node.getChild('c').isIgnored()
	}
}
