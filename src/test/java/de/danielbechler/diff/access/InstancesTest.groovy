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

package de.danielbechler.diff.access

import spock.lang.Specification
import spock.lang.Unroll

class InstancesTest extends Specification {

	def "getType: throws IllegalArgumentException if base and working have incompatible types"() {
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
	def "getType: returns '#resultTypeName' for base of type '#baseClassName' and working of type '#workingClassName'"() {
		setup:
		  def instances = Instances.of(working, base)

		expect:
		  instances.getType() == resultType

		where:
		  base            | working             || resultType
		  new ArrayList() | new ArrayList()     || ArrayList.class
		  new ArrayList() | new LinkedHashSet() || Collection.class
		  new HashMap()   | new TreeMap()       || Map.class

		  baseClassName = base.getClass().getSimpleName()
		  workingClassName = working.getClass().getSimpleName()
		  resultTypeName = resultType.getSimpleName()
	}

	@Unroll
	def "getType: returns type of TypeAwareAccessor when it is primitive '#type'"() {
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

	def "getType: returns most specific shared type for non-primitive objects (except for Map and Collection)"() {
		given:
		  def instances = Instances.of(propertyAccessor, working, base);

		expect:
		  instances.getType() == resultType

		where:
		  propertyAccessor      | working                                    | base                                     | resultType
		  RootAccessor.instance | BigInteger.ONE                             | BigDecimal.ONE                           | Number
		  RootAccessor.instance | new LineNumberReader(new StringReader("")) | new BufferedReader(new StringReader("")) | BufferedReader
		  RootAccessor.instance | new StringReader("")                       | new BufferedReader(new StringReader("")) | Reader
	}

	def "getType: uses type of TypeAwareAccessor if no shared type for non-primitive objects could be determined"() {
		given:
		  def accessor = Mock(TypeAwareAccessor)
		  def instances = Instances.of(accessor, new StringBuilder(), "");

		and:
		  accessor.getType() >>> CharSequence

		expect:
		  instances.getType() == CharSequence
	}

	def 'areNull: returns true when base and working are null'() {
		given:
		  Instances instances = new Instances(RootAccessor.instance, null, null, null);
		expect:
		  instances.areNull()
	}

	def 'areNull: returns false when base is not null'() {
		given:
		  Instances instances = new Instances(RootAccessor.instance, null, '', null);
		expect:
		  !instances.areNull()
	}

	def 'areNull: returns false when working is not null'() {
		given:
		  Instances instances = new Instances(RootAccessor.instance, '', null, null);
		expect:
		  !instances.areNull()
	}

	@Unroll
	def 'isPrimitiveType: returns true for primitive type (#type)'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> type
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, null)
		expect:
		  instances.isPrimitiveType()
		where:
		  type << [boolean, short, int, char, long, double, float, byte]
	}

	@Unroll
	def 'isPrimitiveType: returns returns false for primitive wrapper type (#type)'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> type
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, null)
		expect:
		  !instances.isPrimitiveType()
		where:
		  type << [Boolean, Short, Integer, Character, Long, Double, Float, Byte]
	}

	@Unroll
	def 'isPrimitiveType: returns returns false for complex type'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> type
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, null)
		expect:
		  !instances.isPrimitiveType()
		where:
		  type << [String, Object, Date]
	}

	def 'getFresh: returns zero for numeric types without custom initialization'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> type
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, null)
		expect:
		  instances.getFresh() == 0
		where:
		  type << [short, int, char, long, double, float, byte]
	}

	def 'getFresh: returns false for primitive boolean type without custom initialization'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> boolean
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, null)
		expect:
		  instances.getFresh() == false
	}

	def 'getFresh: returns fresh value from constructor for non-primitive types'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> Object
		and:
		  def instances = new Instances(typeAwareAccessor, null, null, 'foo')
		expect:
		  instances.getFresh() == 'foo'
	}

	@Unroll
	def 'getFresh: returns null for primitive wrapper types (#type)'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> type
		and:
		  def instances = Instances.of(typeAwareAccessor, null, null)
		expect:
		  instances.getFresh() == null
		where:
		  type << [Boolean, Short, Integer, Character, Long, Double, Float, Byte]
	}

	def 'areSame: should return true when working and base are the same object'() {
		given:
		  def object = new Object()
		  def instances = new Instances(RootAccessor.instance, object, object, null)
		expect:
		  instances.areSame()
	}

	def 'areSame: should return false when working and base are not the same object'() {
		given:
		  def instances = new Instances(RootAccessor.instance, new Object(), new Object(), null)
		expect:
		  !instances.areSame()
	}

	@Unroll('hasBeenAdded: should return #result when [working: #working, base: #base, fresh: #fresh]')
	def 'hasBeenAdded'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> typeFromAccessor
		and:
		  def instances = new Instances(typeAwareAccessor, working, base, fresh)
		expect:
		  instances.hasBeenAdded() == result
		where:
		  working | base  | fresh | typeFromAccessor || result
		  null    | null  | null  | null             || false
		  'foo'   | null  | null  | null             || true
		  'foo'   | 'foo' | null  | null             || false
		  null    | 'foo' | null  | null             || false
		  2       | 0     | 0     | int              || true
		  2       | 1     | 1     | int              || true
		  1       | 1     | 1     | int              || false
		  2       | 0     | 1     | int              || false
	}

	@Unroll('hasBeenRemoved: should return #result when [working: #working, base: #base, fresh: #fresh]')
	def 'hasBeenRemoved'() {
		given:
		  def typeAwareAccessor = Stub TypeAwareAccessor
		  typeAwareAccessor.type >> typeFromAccessor
		and:
		  def instances = Instances.of(typeAwareAccessor, working, base, fresh)
		expect:
		  instances.hasBeenRemoved() == result
		where:
		  working | base  | fresh | typeFromAccessor || result
		  null    | null  | null  | null             || false
		  'foo'   | null  | null  | null             || false
		  'foo'   | 'foo' | null  | null             || false
		  null    | 'foo' | null  | null             || true
		  0       | 0     | 0     | int              || false
		  1       | 1     | 1     | int              || false
		  0       | 1     | 1     | int              || false
		  1       | 2     | 1     | int              || true
	}

	def 'access: fails with IllegalArgumentException when accessor is null'() {
		when:
		  new Instances(Mock(Accessor), 'a', 'b', 'c').access(null)
		then:
		  thrown IllegalArgumentException
	}

	def 'access: returns new instance created by using the given accessor'() {
		given:
		  def instances = new Instances(Mock(Accessor), 'working', 'base', 'fresh')
		  def accessor = Stub Accessor, {
			  get('working') >> 'working2'
			  get('base') >> 'base2'
			  get('fresh') >> 'fresh2'
		  }
		when:
		  def accessedInstances = instances.access(accessor)
		then:
		  accessedInstances.working == 'working2'
		  accessedInstances.base == 'base2'
		  accessedInstances.fresh == 'fresh2'
		  accessedInstances.sourceAccessor.is accessor
	}
}
