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

package de.danielbechler.diff.node.path

import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class RootElementSelectorTest extends Specification {
	def 'equals should be true'() {
		expect:
		  RootElementSelector.instance.equals(instance)

		where:
		  instance << [RootElementSelector.instance, new RootElementSelector()]
	}

	def 'equals should be false'() {
		expect:
		  !RootElementSelector.instance.equals(null)
	}

	def 'toHumanReadableString should always return empty string'() {
		expect:
		  RootElementSelector.instance.toHumanReadableString() == ''
	}

	def 'getInstance should always return the same instance'() {
		expect:
		  RootElementSelector.instance == RootElementSelector.instance
	}

	def 'hashCode should always be 0'() {
		expect:
		  RootElementSelector.instance.hashCode() == 0
	}
}
