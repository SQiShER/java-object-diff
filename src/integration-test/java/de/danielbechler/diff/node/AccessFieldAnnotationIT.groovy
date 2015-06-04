/*
 * Copyright 2015 Daniel Bechler
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

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import spock.lang.Specification

class AccessFieldAnnotationIT extends Specification {

	ObjectDiffer objectDiffer = ObjectDifferBuilder.buildDefault()

	def 'access field annotation'() {
		given:
		  def working = new Foo(value: 'working')
		  def base = new Foo(value: 'base')
		when:
		  DiffNode node = objectDiffer.compare(working, base)
		then:
		  node.getChild('value').getFieldAnnotation(TestAnnotation).value() == "it works"
	}

	class Foo {
		@TestAnnotation("it works")
		private String value

		public String getValue() {
			return value
		}

		public void setValue(String value) {
			this.value = value
		}
	}
}
