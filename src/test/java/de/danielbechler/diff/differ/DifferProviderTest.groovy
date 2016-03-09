/*
 * Copyright 2012 Daniel Bechler
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

package de.danielbechler.diff.differ

import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
public class DifferProviderTest extends Specification {

	def differProvider = new DifferProvider()

	def 'return differ that accepts given type'() {
		setup:
		  def differMock = Mock(Differ) {
			  accepts(String) >> true
		  }
		  differProvider.push(differMock)
		expect:
		  differProvider.retrieveDifferForType(String).is(differMock)
	}

	def 'return the last pushed differ that accepts the given type'() {
		given:
		  def differ1 = Mock(Differ) {
			  accepts(String) >> true
		  }
		  differProvider.push(differ1)
		and:
		  def differ2 = Mock(Differ) {
			  accepts(String) >> true
		  }
		  differProvider.push(differ2)
		expect:
		  differProvider.retrieveDifferForType(String).is(differ1) == false
		  differProvider.retrieveDifferForType(String).is(differ2) == true
	}

	def 'throw IllegalArgumentException if no type is given'() {
		when:
		  differProvider.retrieveDifferForType(null)
		then:
		  thrown(IllegalArgumentException)
	}

	def 'throw IllegalStateException if no differ exists for given type'() {
		given:
		  differProvider.push(Stub(Differ) {
			  accepts(String) >> true
		  })
		when:
		  differProvider.retrieveDifferForType(Date)
		then:
		  Exception ex = thrown(IllegalStateException)
		  ex.message == "Couldn't find a differ for type: java.util.Date"
	}
}
