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

package de.danielbechler.diff.introspection

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Subject

@Subject(PropertyAccessExceptionHandler)
class HandlingPropertyAccessExceptionsAT extends Specification {

	def exceptionHandler = Mock(PropertyAccessExceptionHandler)
	def objectDiffer = ObjectDifferBuilder
			.startBuilding()
			.introspection()
			.handlePropertyAccessExceptionsUsing(exceptionHandler)
			.and().build()

	@SuppressWarnings("GroovyAssignabilityCheck")
	def 'exceptions that occur when accessing getters can be handled via PropertyAccessExceptionHandler'() {
		given:
		  def working = new StubForPropertyAccessExceptionTesting(value: 'foo', exceptionOnRead: true)
		  def base = new StubForPropertyAccessExceptionTesting(value: 'bar', exceptionOnRead: true)

		when:
		  objectDiffer.compare(working, base)

		then:
		  1 * exceptionHandler.onPropertyReadException(_ as PropertyReadException, _ as DiffNode) >> {
			  PropertyReadException exception = it[0]
			  assert exception.message == "Failed to read value from property 'value' of type " +
					  "'de.danielbechler.diff.introspection.HandlingPropertyAccessExceptionsAT.StubForPropertyAccessExceptionTesting'"
			  assert exception.targetType == StubForPropertyAccessExceptionTesting
			  assert exception.propertyName == 'value'

			  DiffNode node = it[1]
			  assert node.propertyName == 'value'
			  assert node.path.matches(NodePath.with('value'))
			  assert node.state == DiffNode.State.INACCESSIBLE
		  }
	}

	def 'exceptions that occur when accessing setters can be handled by catching the thrown PropertyWriteException'() {
		given:
		  def working = new StubForPropertyAccessExceptionTesting(value: 'foo')
		  def base = new StubForPropertyAccessExceptionTesting(value: 'bar')
		  def diff = objectDiffer.compare(working, base)

		expect:
		  diff.getChild('value') != null

		when:
		  def target = new StubForPropertyAccessExceptionTesting(exceptionOnWrite: true)
		  diff.getChild('value').canonicalSet(target, 'baz')

		then:
		  PropertyWriteException exception = thrown(PropertyWriteException)
		  exception.newValue == 'baz'
		  exception.message == "Failed to write new value 'baz' to property 'value' of type " +
				  "'de.danielbechler.diff.introspection.HandlingPropertyAccessExceptionsAT.StubForPropertyAccessExceptionTesting'"
		  exception.targetType == StubForPropertyAccessExceptionTesting
		  exception.propertyName == 'value'
	}

	private static class StubForPropertyAccessExceptionTesting {
		String value
		def exceptionOnRead = false
		def exceptionOnWrite = false

		String getValue() {
			if (exceptionOnRead) {
				throw new RuntimeException()
			}
			return value;
		}

		void setValue(String value) {
			if (exceptionOnWrite) {
				throw new RuntimeException()
			}
			this.value = value
		}
	}
}
