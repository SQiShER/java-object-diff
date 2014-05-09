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

import spock.lang.Specification

/**
 * Created by Daniel Bechler.
 */
class PropertyReadExceptionTest extends Specification {
	def throwable = Mock(Throwable)
	def exception = new PropertyReadException('value', ObjectWithValue, throwable)

	def 'should just work'() {
		expect:
		  exception.propertyName == 'value'
		  exception.targetType == ObjectWithValue
		  exception.cause.is throwable
	}

	def 'getMessage should print useful message'() {
		expect:
		  exception.message == "Failed to read value from property 'value' " +
				  "of type 'de.danielbechler.diff.introspection.PropertyReadExceptionTest.ObjectWithValue'"
	}

	class ObjectWithValue {
		def value
	}
}
