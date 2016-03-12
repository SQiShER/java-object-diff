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

package de.danielbechler.util

import de.danielbechler.diff.mock.ObjectWithExceptionThrowingDefaultConstructor
import de.danielbechler.diff.mock.ObjectWithPrivateDefaultConstructor
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.mock.ObjectWithoutDefaultConstructor
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.text.Collator
import java.util.concurrent.ConcurrentSkipListMap

class ClassesTest extends Specification {

	@Shared
	static def PRIMITIVE_WRAPPER_TYPE = [Integer, Short, Character, Long, Boolean, Byte, Float, Double]
	@Shared
	static def COMPLEX_TYPES = [Scanner, Collator, List, Object]
	@Shared
	static def SIMPLE_TYPES = [
			int, short, char, long, boolean, byte, float, double,
			Integer, Short, Character, Long, Boolean, Byte, Float, Double,
			CharSequence, String, URL, Locale, Class
	]

	@Unroll
	def "mostSpecificSharedType: should return #expectedResult for #types"() {
		expect:
		  Classes.mostSpecificSharedType(types) == expectedResult

		where:
		  types                                     || expectedResult
		  [TreeMap, TreeMap]                        || TreeMap
		  [ConcurrentSkipListMap, TreeMap]          || AbstractMap
		  [ConcurrentSkipListMap, TreeMap, HashMap] || AbstractMap
		  [ArrayList, AbstractList]                 || AbstractList
		  [CharSequence, String]                    || CharSequence
		  [Serializable, Serializable]              || Serializable
		  [String, Map, Date]                       || null
	}

	def 'allAssignableFrom: should return true if all items share the expected type'() {
		given:
		  def types = [ArrayList, LinkedList]
		expect:
		  Classes.allAssignableFrom(List, types) == true;
	}

	def 'allAssignableFrom: should return false if not all items share the expected type'() {
		given:
		  def types = [Object, LinkedList]
		expect:
		  Classes.allAssignableFrom(List, types) == false
	}

	@Unroll
	def 'isPrimitiveWrapperType: should return true for #type'() {
		expect:
		  Classes.isPrimitiveWrapperType(type) == true
		where:
		  type << PRIMITIVE_WRAPPER_TYPE
	}

	def 'isPrimitiveWrapperType: should return false for other types'() {
		expect:
		  Classes.isPrimitiveWrapperType(type) == false
		where:
		  type << COMPLEX_TYPES
	}

	@Unroll
	def 'isSimpleType: should return true for type #type'() {
		expect:
		  Classes.isSimpleType(type)
		where:
		  type << SIMPLE_TYPES
	}

	def 'isSimpleType: should return false for other types'() {
		expect:
		  !Classes.isSimpleType(type)
		where:
		  type << COMPLEX_TYPES
	}

	def 'freshInstanceOf: should return new instance of desired type'() {
		expect:
		  Classes.freshInstanceOf(ObjectWithString) instanceof ObjectWithString
	}

	def 'freshInstanceOf: should return new instance of desired type (even if it has a private default constructor)'() {
		expect:
		  Classes.freshInstanceOf(ObjectWithPrivateDefaultConstructor) instanceof ObjectWithPrivateDefaultConstructor
	}

	def 'freshInstanceOf: should return null when desired type has no default constructor'() {
		expect:
		  Classes.freshInstanceOf(ObjectWithoutDefaultConstructor) == null
	}

	def 'freshInstanceOf: should return null when desired type is null'() {
		expect:
		  Classes.freshInstanceOf(null) == null
	}

	def 'freshInstanceOf: should fail with exception if constructor throws exception'() {
		when:
		  Classes.freshInstanceOf(ObjectWithExceptionThrowingDefaultConstructor)
		then:
		  thrown(RuntimeException)
	}
}
