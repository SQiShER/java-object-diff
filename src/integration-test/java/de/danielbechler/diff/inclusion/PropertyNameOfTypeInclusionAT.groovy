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
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class PropertyNameOfTypeInclusionAT extends Specification {

	def "including properties implicitly excludes other properties from the type"() {
		given:
		  def base = new Apple(foo: '1', bar: '1', baz: '1')
		  def working = new Apple(foo: '2', bar: '2', baz: '2')

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .inclusion().include()
				  .propertyNameOfType(Apple, "foo")
				  .propertyNameOfType(Apple, "bar").and()
				  .build()
				  .compare(working, base)

		then:
		  node.childCount() == 2
		  node.getChild('foo').state == DiffNode.State.CHANGED
		  node.getChild('bar').state == DiffNode.State.CHANGED
	}

	def "including properties does not affect properties of other types"() {
		given:
		  def base = new Orange(baz: 'original')
		  def working = new Orange(baz: 'changed')

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .inclusion().include()
				  .propertyNameOfType(Apple, "foo")
				  .propertyNameOfType(Apple, "bar").and()
				  .build()
				  .compare(working, base)

		then:
		  node.getChild('baz').state == DiffNode.State.CHANGED
	}

	def "excluding properties works too"() {
		given:
		  def base = new Apple(foo: '1', bar: '1', baz: '1')
		  def working = new Apple(foo: '2', bar: '2', baz: '1')

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .inclusion().exclude()
				  .propertyNameOfType(Apple, "foo").and()
				  .build()
				  .compare(working, base)

		then:
		  node.childCount() == 1
		  node.getChild('bar').state == DiffNode.State.CHANGED
	}

	def "excluding properties also does not apply to properties of other types"() {
		given:
		  def base = new Apple(foo: 'to-be-changed', bar: 'to-be-changed', baz: 'to-be-changed')
		  def working = new Apple(foo: 'changed', bar: 'changed', baz: 'changed')

		when:
		  def node = ObjectDifferBuilder.startBuilding()
				  .inclusion().exclude()
				  .propertyNameOfType(Orange, "baz").and()
				  .build()
				  .compare(working, base)

		then:
		  node.childCount() == 3
		  node.getChild('foo').state == DiffNode.State.CHANGED
		  node.getChild('bar').state == DiffNode.State.CHANGED
		  node.getChild('baz').state == DiffNode.State.CHANGED
	}

	static class Apple {
		def foo;
		def bar;
		def baz;
	}

	static class Orange {
		def baz;
	}
}
