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
import de.danielbechler.diff.introspection.ObjectDiffProperty
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class PropertyNameOfTypeInclusionViaAnnotationAT extends Specification {

	def "including properties implicitly excludes other properties"() {
		given:
		  def base = new AppleWithIncludedProperties(foo: '1', bar: '1', baz: '1')
		  def working = new AppleWithIncludedProperties(foo: '2', bar: '2', baz: '2')

		when:
		  def node = ObjectDifferBuilder.startBuilding().build().compare(working, base)

		then:
		  node.childCount() == 2
		  node.getChild('foo').state == DiffNode.State.CHANGED
		  node.getChild('bar').state == DiffNode.State.CHANGED
	}

	static class AppleWithIncludedProperties {
		def foo;
		def bar;
		def baz;

		@ObjectDiffProperty(inclusion = Inclusion.INCLUDED)
		def getFoo() {
			return foo
		}

		void setFoo(foo) {
			this.foo = foo
		}

		@ObjectDiffProperty(inclusion = Inclusion.INCLUDED)
		def getBar() {
			return bar
		}

		void setBar(bar) {
			this.bar = bar
		}
	}

	def "excluding properties doesn't affect other properties"() {
		given:
		  def base = new AppleWithExcludedProperties(foo: '1', bar: '1', baz: '1')
		  def working = new AppleWithExcludedProperties(foo: '2', bar: '2', baz: '1')

		when:
		  def node = ObjectDifferBuilder.startBuilding().build().compare(working, base)

		then:
		  node.childCount() == 1
		  node.getChild('bar').state == DiffNode.State.CHANGED
	}

	static class AppleWithExcludedProperties {
		def foo;
		def bar;
		def baz;

		@ObjectDiffProperty(inclusion = Inclusion.EXCLUDED)
		def getFoo() {
			return foo
		}

		void setFoo(foo) {
			this.foo = foo
		}
	}
}
