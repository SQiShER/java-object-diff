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

package de.danielbechler.diff.access

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author Daniel Bechler
 */
class InstancesSpec extends Specification {
	def "getType() throws IllegalArgumentException if base and working have incompatible types"() {
		setup:
		  def instances = new Instances(RootAccessor.instance, working, base, null)

		when:
		  instances.getType()

		then:
		  thrown(IllegalArgumentException)

		where:
		  base                | working
		  new StringBuilder() | ""
		  "foo"               | 42
		  true                | new Date()
	}

	@Unroll
	def "getType() returns '#resultTypeName' for base of type '#baseClassName' and working of type '#workingClassName'"() {
		setup:
		  def instances = Instances.of(working, base)

		expect:
		  instances.getType() == resultType

		where:
		  base            | working             || resultType
		  new ArrayList() | new LinkedHashSet() || Collection.class
		  new HashMap()   | new TreeMap()       || Map.class

		  baseClassName = base.getClass().getSimpleName()
		  workingClassName = working.getClass().getSimpleName()
		  resultTypeName = resultType.getSimpleName()
	}

	@Unroll
	def "getType() returns type of TypeAwareAccessor when it is primitive '#type'"() {
		given:
		  def typeAwareAccessor = Mock(TypeAwareAccessor);
		  def instances = Instances.of(typeAwareAccessor, 0, 0L);

		and:
		  1 * typeAwareAccessor.getType() >>> type

		expect:
		  instances.getType() == type

		where:
		  type << [long, int, float, boolean, char, byte, short]
	}

	def "getType() returns most specific shared type for non-primitive objects (except for Map and Collection)"() {
		given:
		  def instances = Instances.of(accessor, working, base);

		expect:
		  instances.getType() == resultType

		where:
		  accessor              | working                                    | base                                     | resultType
		  RootAccessor.instance | BigInteger.ONE                             | BigDecimal.ONE                           | Number
		  RootAccessor.instance | new LineNumberReader(new StringReader("")) | new BufferedReader(new StringReader("")) | BufferedReader
		  RootAccessor.instance | new StringReader("")                       | new BufferedReader(new StringReader("")) | Reader
	}

	def "getType() uses type of TypeAwareAccessor if no shared type for non-primitive objects could be determined"() {
		given:
		  def accessor = Mock(TypeAwareAccessor)
		  def instances = Instances.of(accessor, new StringBuilder(), "");

		and:
		  accessor.getType() >>> CharSequence

		expect:
		  instances.getType() == CharSequence
	}
}
