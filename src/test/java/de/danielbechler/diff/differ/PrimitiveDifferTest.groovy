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

package de.danielbechler.diff.differ

import de.danielbechler.diff.access.Instances
import de.danielbechler.diff.access.TypeAwareAccessor
import de.danielbechler.diff.comparison.PrimitiveDefaultValueModeResolver
import de.danielbechler.diff.node.DiffNode
import spock.lang.Specification
import spock.lang.Unroll

import static de.danielbechler.diff.comparison.PrimitiveDefaultValueMode.ASSIGNED
import static de.danielbechler.diff.comparison.PrimitiveDefaultValueMode.UNASSIGNED
import static de.danielbechler.diff.node.DiffNode.State.*

public class PrimitiveDifferTest extends Specification {

	PrimitiveDefaultValueModeResolver primitiveDefaultValueModeResolver = Mock(PrimitiveDefaultValueModeResolver)
	PrimitiveDiffer primitiveDiffer = new PrimitiveDiffer(primitiveDefaultValueModeResolver)

	@Unroll('in #mode mode #type values get marked as #state when #scenarioSummary')
	def 'detects state based on the given PrimitiveDefaultValueMode'() {
		given:
		  primitiveDefaultValueModeResolver.resolvePrimitiveDefaultValueMode(_ as DiffNode) >> mode
		and:
		  def typeAwareAccessor = Stub(TypeAwareAccessor, {
			  getType() >> type
		  })
		  def instances = Instances.of(typeAwareAccessor, working, base, fresh);
		when:
		  def node = primitiveDiffer.compare(DiffNode.ROOT, instances)
		then:
		  node.state == state
		where:
		  mode       | type    | base  | working | fresh || state
		  UNASSIGNED | int     | 0     | 0       | 0     || UNTOUCHED
		  UNASSIGNED | int     | 0     | 1       | 0     || ADDED
		  UNASSIGNED | int     | 1     | 0       | 0     || REMOVED
		  UNASSIGNED | int     | 1     | 2       | 0     || CHANGED
		  UNASSIGNED | int     | 2     | 2       | 0     || UNTOUCHED

		  UNASSIGNED | long    | 1L    | 1L      | 0L    || UNTOUCHED
		  UNASSIGNED | long    | 0L    | 1L      | 0L    || ADDED
		  UNASSIGNED | long    | 1L    | 0L      | 0L    || REMOVED
		  UNASSIGNED | long    | 1L    | 2L      | 0L    || CHANGED
		  UNASSIGNED | long    | 2L    | 2L      | 0L    || UNTOUCHED

		  UNASSIGNED | float   | 0.0F  | 0.0F    | 0.0F  || UNTOUCHED
		  UNASSIGNED | float   | 0.0F  | 1.0F    | 0.0F  || ADDED
		  UNASSIGNED | float   | 1.0F  | 0.0F    | 0.0F  || REMOVED
		  UNASSIGNED | float   | 1.0F  | 2.0F    | 0.0F  || CHANGED
		  UNASSIGNED | float   | 2.0F  | 2.0F    | 0.0F  || UNTOUCHED

		  UNASSIGNED | double  | 0.0D  | 0.0D    | 0.0D  || UNTOUCHED
		  UNASSIGNED | double  | 0.0D  | 1.0D    | 0.0D  || ADDED
		  UNASSIGNED | double  | 1.0D  | 0.0D    | 0.0D  || REMOVED
		  UNASSIGNED | double  | 1.0D  | 2.0D    | 0.0D  || CHANGED
		  UNASSIGNED | double  | 2.0D  | 2.0D    | 0.0D  || UNTOUCHED

		  UNASSIGNED | boolean | false | false   | false || UNTOUCHED
		  UNASSIGNED | boolean | false | true    | false || ADDED
		  UNASSIGNED | boolean | true  | false   | false || REMOVED
		  UNASSIGNED | boolean | true  | true    | false || UNTOUCHED

		  ASSIGNED   | int     | 0     | 0       | 0     || UNTOUCHED
		  ASSIGNED   | int     | 0     | 1       | 0     || CHANGED
		  ASSIGNED   | int     | 1     | 0       | 0     || CHANGED
		  ASSIGNED   | int     | 1     | 2       | 0     || CHANGED
		  ASSIGNED   | int     | 2     | 2       | 0     || UNTOUCHED

		  ASSIGNED   | long    | 0L    | 0L      | 0L    || UNTOUCHED
		  ASSIGNED   | long    | 0L    | 1L      | 0L    || CHANGED
		  ASSIGNED   | long    | 1L    | 0L      | 0L    || CHANGED
		  ASSIGNED   | long    | 1L    | 2L      | 0L    || CHANGED
		  ASSIGNED   | long    | 2L    | 2L      | 0L    || UNTOUCHED

		  ASSIGNED   | float   | 0.0F  | 0.0F    | 0.0F  || UNTOUCHED
		  ASSIGNED   | float   | 0.0F  | 1.0F    | 0.0F  || CHANGED
		  ASSIGNED   | float   | 1.0F  | 0.0F    | 0.0F  || CHANGED
		  ASSIGNED   | float   | 1.0F  | 2.0F    | 0.0F  || CHANGED
		  ASSIGNED   | float   | 2.0F  | 2.0F    | 0.0F  || UNTOUCHED

		  ASSIGNED   | double  | 0.0D  | 0.0D    | 0.0D  || UNTOUCHED
		  ASSIGNED   | double  | 0.0D  | 1.0D    | 0.0D  || CHANGED
		  ASSIGNED   | double  | 1.0D  | 0.0D    | 0.0D  || CHANGED
		  ASSIGNED   | double  | 1.0D  | 2.0D    | 0.0D  || CHANGED
		  ASSIGNED   | double  | 2.0D  | 2.0D    | 0.0D  || UNTOUCHED

		  ASSIGNED   | boolean | false | false   | false || UNTOUCHED
		  ASSIGNED   | boolean | false | true    | false || CHANGED
		  ASSIGNED   | boolean | true  | false   | false || CHANGED
		  ASSIGNED   | boolean | true  | true    | false || UNTOUCHED

		  scenarioSummary = computeScenarioSummary(base, working, fresh)
	}

	String computeScenarioSummary(base, working, fresh) {
		if (working == fresh) {
			if (base == fresh) {
				'base and working equal default value'
			} else {
				'working equals default value but base does not'
			}
		} else if (base == fresh) {
			if (working == fresh) {
				'base and working equal default value'
			} else {
				'base equals default value but working does not'
			}
		} else if (working == base) {
			'base and working are equal but differ from default value'
		} else {
			'base and working are not equal but differ from default value'
		}
	}

	@Unroll
	def 'fails with exception when called for non-primitive type (#type)'() {
		given:
		  def instances = Stub(Instances, {
			  getType() >> type
		  })
		when:
		  primitiveDiffer.compare(DiffNode.ROOT, instances)
		then:
		  thrown IllegalArgumentException
		where:
		  type << [Integer, Boolean, Void, Double, Float, Short, Byte, Date, BigDecimal]
	}

	@Unroll
	def 'accepts only primitive types (#type -> #acceptable)'() {
		expect:
		  primitiveDiffer.accepts(type) == acceptable
		where:
		  type    || acceptable
		  boolean || true
		  int     || true
		  char    || true
		  long    || true
		  float   || true
		  double  || true
		  short   || true
		  byte    || true
		  Boolean || false
		  Integer || false
		  Long    || false
		  Float   || false
		  Double  || false
		  Short   || false
		  Byte    || false
	}

}
