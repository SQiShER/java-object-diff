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

package de.danielbechler.diff.example

import de.danielbechler.diff.builder.ObjectDifferBuilder
import spock.lang.Specification

import static de.danielbechler.diff.node.DiffNode.State.CHANGED
import static de.danielbechler.diff.node.DiffNode.State.UNTOUCHED

public class Example1IT extends Specification {
	def "Comparing Objects via compareTo instead of equals"() {
		given: "an object differ configured to compare the given type via compareTo method"
		  def builder = ObjectDifferBuilder.startBuilding()
		  builder.configure().comparison().ofType(ComparableObject).toUseCompareToMethod()
		  def objectDiffer = builder.build()

		expect:
		  objectDiffer.compare(working, base).state == expectedState

		where:
		  base                           | working                        || expectedState
		  new ComparableObject("foo", 1) | new ComparableObject("foo", 2) || UNTOUCHED
		  new ComparableObject("foo", 1) | new ComparableObject("bar", 1) || CHANGED
	}

	public static class ComparableObject implements Comparable<ComparableObject> {
		private final String value
		private final int index

		public ComparableObject(String value, int index) {
			this.value = value
			this.index = index
		}

		public String getValue() {
			return value
		}

		public int compareTo(ComparableObject o) {
			return value.compareToIgnoreCase(o.value)
		}

		public boolean equals(Object o) {
			return false
		}

		public int hashCode() {
			return 0
		}
	}
}
