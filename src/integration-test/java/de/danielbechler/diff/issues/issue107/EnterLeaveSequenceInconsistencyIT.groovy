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

package de.danielbechler.diff.issues.issue107

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.circular.CircularReferenceDetector
import de.danielbechler.diff.differ.DifferDispatcher
import de.danielbechler.diff.node.PrintingVisitor
import spock.lang.Issue
import spock.lang.Specification
import spock.lang.Subject

@Subject([CircularReferenceDetector, DifferDispatcher])
@Issue("https://github.com/SQiShER/java-object-diff/issues/107")
class EnterLeaveSequenceInconsistencyIT extends Specification {

	def "Causes: 'Detected inconsistency in enter/leave sequence. Must always be LIFO'"() {

		given:
		  ObjectDiffer differ = ObjectDifferBuilder.buildDefault()

		and: "a working version in which 'a2' and 'c2' reference each other"
		  A a2 = new A(s1: "a2")
		  B b1 = new B(s2: "b1")
		  C c1 = new C(s3: "c1", a: a2)
		  A working = new A(s1: "a1", map: [(b1): c1])
		  B b2 = new B(s2: "b2")
		  C c2 = new C(s3: "c2", a: a2)
		  a2.map.put(b2, c2)

		and: "a base version in which 'a3' references 'b1' of the working version and 'a3' and 'c3' reference each other"
		  A base = new A(s1: "a3")
		  C c3 = new C(a: base, s3: "s3")
		  base.map.put(b1, c3)

		when:
		  differ.compare(working, base).visit(new PrintingVisitor(working, base))

		then:
		  noExceptionThrown()
	}

	static class A {
		def String s1
		def Map<B, C> map = [:]

		@Override
		String toString() {
			"A(${s1})"
		}
	}

	static class B {
		def String s2

		@Override
		String toString() {
			"B(${s2})"
		}
	}

	static class C {
		def String s3
		def A a

		@Override
		String toString() {
			"C(${s3})"
		}
	}
}
