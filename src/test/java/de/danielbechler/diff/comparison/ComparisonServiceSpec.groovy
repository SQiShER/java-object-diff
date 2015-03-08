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

package de.danielbechler.diff.comparison

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.path.NodePath
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Time
import java.sql.Timestamp

class ComparisonServiceSpec extends Specification {

	def objectDifferBuilder = Stub(ObjectDifferBuilder)
	ComparisonService comparisonService = new ComparisonService(objectDifferBuilder)

	@Unroll
	def "resolveComparisonStrategy: returns ComparableComparisonStrategy for simple types implementing Comparable (e.g. #type)"() {
		given:
		  def node = Stub(DiffNode)
		  node.valueType >> type
		  node.path >> NodePath.with('any')

		expect:
		  comparisonService.resolveComparisonStrategy(node) instanceof ComparableComparisonStrategy

		where:
		  type << [
				  Boolean,
				  Byte,
				  Character,
				  Double,
				  Enum,
				  Float,
				  Integer,
				  Long,
				  Short,
				  String,
				  BigDecimal,
				  BigInteger,
				  URI,
				  Date,
				  Time,
				  Timestamp,
//				  Duration,
//				  Instant,
//				  LocalDate,
//				  LocalDateTime,
//				  LocalTime,
//				  MonthDay,
//				  OffsetDateTime,
//				  OffsetTime,
//				  Year,
//				  YearMonth,
//				  ZonedDateTime,
//				  ZoneOffset,
				  Calendar,
				  Date,
				  GregorianCalendar,
				  UUID
		  ]
	}

	def 'resolveComparisonStrategy: should not return ComparableComparisonStrategy for non-simple types implementing Comparable'() {
		given:
		  def node = Stub(DiffNode)
		  node.valueType >> type
		  node.path >> NodePath.with('any')
		  node.getPropertyAnnotation(_ as Class) >> null

		expect:
		  comparisonService.resolveComparisonStrategy(node) == null

		where:
		  type << [CustomComparable]
	}

	public static class CustomComparable implements Comparable<CustomComparable> {
		@Override
		int compareTo(CustomComparable o) {
			return 0
		}
	}
}
