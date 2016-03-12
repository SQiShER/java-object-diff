/*
 * Copyright 2016 Daniel Bechler
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

import spock.lang.Specification

class StringsTest extends Specification {

	def "join: joins parts with given delimiter"() {
		expect:
		  Strings.join(':', ['a', 'b', 'c']) == 'a:b:c'
	}

	def "join: ignores null elements"() {
		expect:
		  Strings.join(':', ['a', 'b', null, 'c']) == 'a:b:c'
	}

	def "join: returns empty string when given null"() {
		expect:
		  Strings.join(':', null) == ''
	}
}
