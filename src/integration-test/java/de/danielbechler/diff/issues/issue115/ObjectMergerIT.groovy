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

package de.danielbechler.diff.issues.issue115

import de.danielbechler.diff.ObjectMerger
import groovy.transform.ToString
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Subject

@Issue('https://github.com/SQiShER/java-object-diff/issues/115')
@Subject(ObjectMerger)
class ObjectMergerIT extends Specification {

	def 'should write changes to delta object and create missing parent objects along the way'() {
		given:
		  A base = new A();
		  base.b = new B();
		  base.b.y = 10;
		  base.x = 10;

		and:
		  A change = new A();
		  change.b = new B();
		  change.b.y = 12;
		  change.x = 10;


		when:
		  def objectMerger = new ObjectMerger()
		  def delta = new A()
		  objectMerger.merge(change, base, delta);

		then:
		  delta.b instanceof B
		  delta.b.y == 12
	}

	@ToString
	public static class A {
		int x
		B b

		public A() {}
	}

	@ToString
	public static class B {
		int y

		public B() {}
	}
}
