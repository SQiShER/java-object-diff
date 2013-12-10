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

package de.danielbechler.diff.map

import spock.lang.Specification
import spock.lang.Unroll

class MapElementTest extends Specification {

    def "should be constructable with 'null'"() {
        when:
        new MapElement(null)

        then:
        notThrown(Throwable)
    }

    def "should be equal to self"() {
        setup:
        def element = new MapElement('foo')

        expect:
        element.equals(element)
    }

    @Unroll
    def "equals should be #expected when keys are #key and #anotherKey"() {
        given:
        def element = new MapElement(key)
        def anotherElement = new MapElement(anotherKey)

        expect:
        element.equals(anotherElement) == expected

        where:
        key   | anotherKey || expected
        null  | null       || true
        null  | 'foo'      || false
        'foo' | null       || false
        'foo' | 'foo'      || true
        'foo' | 'bar'      || false
    }

    def "should not be equal to 'null'"() {
        expect:
        !new MapElement('foo').equals(null)
    }

    def "should not be equal to different types"() {
        expect:
        !new MapElement('foo').equals(2)
    }

    def "hashCode should equal the hashCode of the key"() {
        given:
        def key = new Object()

        expect:
        new MapElement(key).hashCode() == key.hashCode()
    }

    def "hashCode should be null when key is null"() {
        expect:
        new MapElement(null).hashCode() == 0
    }

    def "toString should return the same as toHumanReadableString()"() {
        setup:
        def element = new MapElement('foo')

        expect:
        element.toString() == element.toHumanReadableString()
    }

    def "getKey should return the object passed to the constructor"() {
        setup:
        def key = new Object()

        expect:
        new MapElement(key).key.is(key)
    }

    def "toHumanReadableString should return the string representation of the key (without line breaks)"() {
        expect:
        new MapElement("foo,\nbar").toHumanReadableString() == "{foo, \\ bar}"
    }
}
