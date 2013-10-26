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

package de.danielbechler.util

import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class ComparablesSpec extends Specification
{
  def "isEqualByComparison should return true when both parameters are null"()
  {
    expect:
    Comparables.isEqualByComparison(null, null)
  }

  def "isEqualByComparison should return true when both arguments are equal by comparison"()
  {
    expect:
    Comparables.isEqualByComparison(new BigDecimal(10), new BigDecimal(10))
  }

  def "isEqualByComparison should be null-safe even if compareTo method of comparable types is not"()
  {
    when:
    def result = Comparables.isEqualByComparison(a, b)

    then:
    !result

    where:
    a              | b
    BigDecimal.ONE | null
    null           | BigDecimal.TEN
  }
}
