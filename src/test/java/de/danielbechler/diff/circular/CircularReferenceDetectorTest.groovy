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

package de.danielbechler.diff.circular

import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Unroll

import static de.danielbechler.diff.circular.CircularReferenceDetector.CircularReferenceException
import static de.danielbechler.diff.circular.CircularReferenceDetector.ReferenceMatchingMode.EQUALITY_OPERATOR
import static de.danielbechler.diff.circular.CircularReferenceDetector.ReferenceMatchingMode.EQUALS_METHOD

class CircularReferenceDetectorTest extends Specification {

	CircularReferenceDetector circularReferenceDetector = new CircularReferenceDetector(EQUALITY_OPERATOR)

	def 'push: does nothing with null object'() {
		when:
		  circularReferenceDetector.push(null, null)
		then:
		  circularReferenceDetector.size() == 0
	}

	def 'push: adds unknown object to stack'() {
		when:
		  circularReferenceDetector.push("foo", NodePath.withRoot())
		then:
		  circularReferenceDetector.size() == 1
	}

	def 'push: throws CircularReferenceException when given known object'() {
		given:
		  def rootPath = NodePath.withRoot()
		  circularReferenceDetector.push("foo", rootPath)
		when:
		  circularReferenceDetector.push("foo", NodePath.with("test"))
		then: 'exception should be thrown'
		  CircularReferenceException ex = thrown CircularReferenceException
		  ex.nodePath == rootPath
		and: 'the known object should not be added again'
		  circularReferenceDetector.size() == 1

	}

	def 'remove: does nothing with null object'() {
		expect:
		  circularReferenceDetector.size() == 0
		when:
		  circularReferenceDetector.remove null
		then:
		  circularReferenceDetector.size() == 0
	}

	def 'remove: throws IllegalArgumentException when instance is removed out of order'() {
		given:
		  circularReferenceDetector.push "foo", null
		when:
		  circularReferenceDetector.remove "bar"
		then:
		  thrown IllegalArgumentException
	}

	def 'remove: removes instance when it is removed in order'() {
		given:
		  circularReferenceDetector.push "foo", null
		expect:
		  circularReferenceDetector.size() == 1
		when:
		  circularReferenceDetector.remove "foo"
		then:
		  circularReferenceDetector.size() == 0
	}

	def 'knows: returns true when instance is known'() {
		expect:
		  !circularReferenceDetector.knows('foo')
		when:
		  circularReferenceDetector.push 'foo', null
		then:
		  circularReferenceDetector.knows 'foo'
	}

	def 'knows: returns false for previously added instance which has been removed'() {
		given:
		  circularReferenceDetector.push "foo", null
		expect:
		  circularReferenceDetector.knows("foo") == true
		when:
		  circularReferenceDetector.remove "foo"
		then:
		  circularReferenceDetector.knows("foo") == false
	}

	def 'knows: returns false when instance is unknown'() {
		when:
		  circularReferenceDetector.push 'foo', null
		then:
		  circularReferenceDetector.knows('bar') == false
	}

	@Unroll
	def 'matchingMode #matchingMode'() {
		given:
		  circularReferenceDetector = new CircularReferenceDetector(matchingMode)

		expect: 'sanity check'
		  internalInstance.is(externalInstance) == equalByOperator

		when:
		  circularReferenceDetector.push(internalInstance, NodePath.withRoot());
		then:
		  circularReferenceDetector.knows(externalInstance)

		when:
		  circularReferenceDetector.push(internalInstance, NodePath.withRoot());
		then:
		  thrown CircularReferenceException

		when:
		  circularReferenceDetector.remove(externalInstance)
		then:
		  !circularReferenceDetector.knows(externalInstance)

		expect:
		  circularReferenceDetector.size() == 0

		where:
		  matchingMode      | internalInstance            | externalInstance            || equalByOperator
		  EQUALS_METHOD     | new ObjectWithString("foo") | new ObjectWithString("foo") || false
		  EQUALITY_OPERATOR | 'foo'                       | 'foo'                       || true
	}
}
