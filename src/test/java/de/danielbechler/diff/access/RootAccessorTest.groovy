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

package de.danielbechler.diff.access

import de.danielbechler.diff.selector.RootElementSelector
import spock.lang.Specification

class RootAccessorTest extends Specification {

	def 'get: returns target object'() {
		given:
		  def target = new Object()
		expect:
		  RootAccessor.instance.get(target).is target
	}

	def 'set: fails with UnsupportedOperationException'() {
		given:
		  def target = new Object()
		when:
		  RootAccessor.instance.set(target, 'anything')
		then:
		  thrown UnsupportedOperationException
	}

	def 'unset: fails with UnsupportedOperationException'() {
		given:
		  def target = new Object()
		when:
		  RootAccessor.instance.unset target
		then:
		  thrown UnsupportedOperationException
	}

	def 'getPathElement: returns RootElementSelector'() {
		expect:
		  RootAccessor.instance.elementSelector instanceof RootElementSelector
	}

	def 'toString: returns "root element"'() {
		expect:
		  RootAccessor.instance.toString() == "root element"
	}
}
