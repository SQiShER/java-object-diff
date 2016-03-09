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

package de.danielbechler.diff.issues.issue15

import de.danielbechler.diff.ObjectDiffer
import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.mock.ObjectWithNestedObject
import spock.lang.Specification

import static de.danielbechler.diff.circular.CircularReferenceMatchingMode.EQUALS_METHOD

/**
 * @author Daniel Bechler
 */
public class CircularReferenceDetectionBasedOnEqualsIT extends Specification {

	ObjectDiffer objectDiffer

	def setup() {
		objectDiffer = ObjectDifferBuilder.startBuilding()
				.circularReferenceHandling()
				.matchCircularReferencesUsing(EQUALS_METHOD).and()
				.build()
	}

	def 'detects circular reference when encountering same object twice'() {
		given:
		  def object = new ObjectWithNestedObject('foo')
		  object.object = object
		when:
		  def node = objectDiffer.compare(object, null)
		then:
		  node.getChild('object').isCircular()
	}

	def 'detects circular reference when encountering different but equal objects twice'() {
		given:
		  def object = new ObjectWithNestedObject('foo', new ObjectWithNestedObject('foo'))
		when:
		  def node = objectDiffer.compare(object, null)
		then:
		  node.getChild('object').isCircular()
	}
}
