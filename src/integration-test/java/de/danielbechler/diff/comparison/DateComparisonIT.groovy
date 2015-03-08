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
import spock.lang.Specification
import spock.lang.Unroll

import java.sql.Time
import java.sql.Timestamp

class DateComparisonIT extends Specification {

	@Unroll
	def 'should consider java.util.Date and #subclass equal when they represent the same date'() {
		def now = new Timestamp(System.currentTimeMillis()).time

		given:
		  def utilDate = new Date(now)
		  def sqlDate = subclass.newInstance([now] as Object[])
		expect:
		  ObjectDifferBuilder.buildDefault().compare(utilDate, sqlDate).untouched
		where:
		  subclass << [java.sql.Date, Time, Timestamp]
	}

	def 'should consider java.sql.Time and java.util.Date equal when they represent the same date'() {
		def now = System.currentTimeMillis()

		given:
		  def utilDate = new Date(now)
		  def time = new Time(now)
		expect:
		  ObjectDifferBuilder.buildDefault().compare(utilDate, time).untouched
	}
}
