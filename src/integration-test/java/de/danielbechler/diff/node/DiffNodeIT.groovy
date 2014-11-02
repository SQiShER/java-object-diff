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

package de.danielbechler.diff.node

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.introspection.InstanceFactory
import de.danielbechler.diff.introspection.TypeInstantiationException
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Subject

@Subject(DiffNode)
class DiffNodeIT extends Specification {

	def 'canonicalSet() should create missing objects along the path'() {
		given:
		  def base = new A(b: new B(c: new C(d: new D(x: 'to-be-changed'))))
		  def working = new A(b: new B(c: new C(d: new D(x: 'changed'))))

		and:
		  def instanceFactory = new InstanceFactory() {
			  Object newInstanceOfType(Class<?> type) {
				  if (type.isAssignableFrom(C)) {
					  return C.newInstance()
				  }
				  return null
			  }
		  }

		when:
		  def target = new A()
		  def node = ObjectDifferBuilder.startBuilding()
				  .introspection()
				  .setInstanceFactory(instanceFactory)
				  .and()
				  .build()
				  .compare(working, base)
		  node.getChild(NodePath.with('b', 'c', 'd', 'x')).canonicalSet(target, 'yay')

		then:
		  target.b instanceof B
		  target.b.c instanceof C
		  target.b.c.d instanceof D
		  target.b.c.d.x == 'yay'
	}

	def 'canonicalSet() should throw exception when missing object along the path cannot be instantiated'() {
		given:
		  def base = new A(b: new B(c: new C(d: new D(x: 'to-be-changed'))))
		  def working = new A(b: new B(c: new C(d: new D(x: 'changed'))))

		when:
		  def target = new A()
		  def node = ObjectDifferBuilder.buildDefault().compare(working, base)
		  node.getChild(NodePath.with('b', 'c', 'd', 'x')).canonicalSet(target, 'yay')

		then:
		  TypeInstantiationException exception = thrown(TypeInstantiationException)
		  exception.type == C
		  exception.reason == "Type doesn't have a public non-arg constructur"
		  exception.message == "Failed to create instance of type '" + exception.type + "'. Reason: " + exception.reason
	}

	public static class A {
		B b
	}

	public static class B {
		C c
	}

	public static class C {
		D d

		private C() {
		}

		public static C newInstance() {
			return new C()
		}
	}

	public static class D {
		def x
	}
}
