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

package de.danielbechler.diff.map

import de.danielbechler.diff.mock.ObjectWithIdentityAndValue
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class MapEntryAccessorTest extends Specification {

    @Shared
    def key1 = new ObjectWithIdentityAndValue("key", "1")
    @Shared
    def key2 = new ObjectWithIdentityAndValue("key", "2")
    def accessor = new MapEntryAccessor("b");

    def "fail on construction with null referenceKey"() {
        when:
        new MapEntryAccessor(null);

        then:
        thrown(IllegalArgumentException)
    }

    def "provide access to its path element"() {
        expect:
        accessor.getElementSelector() instanceof MapKeyElementSelector
    }

    def "provide write access to referenced value in any map"() {
        given:
        def map = [] as TreeMap

        when:
        accessor.set(map, "foo")

        then:
        map.get("b") == "foo"

        when:
        accessor.set(map, "bar")

        then:
        map.get("b") == "bar"
    }

    def "provide read access to referenced value in any map"() throws Exception {
        given:
        def map = [] as TreeMap

        when:
        map.put("b", "foo");

        then:
        accessor.get(map) == "foo"
    }

    def "throw exception when trying to read from non map object"() {
        when:
        accessor.get(new Object())

        then:
        thrown(IllegalArgumentException)
    }

    def "return null when reading from null object"() {
        expect:
        accessor.get(null) == null
    }

    def "remove referenced entry from any map"() {
        given:
        def map = [] as TreeMap

        when:
        map.put("b", "foo")

        and:
        accessor.unset(map)

        then:
        map.isEmpty()
    }

    def "return the key object of the given map"() {
        given:
        accessor = new MapEntryAccessor(referenceKey)

        and:
        def map = [] as HashMap
        map.put actualMapKey, "foo"

        expect:
        accessor.getKey(map).is actualMapKey

        where:
        referenceKey | actualMapKey
        key1         | key2
        key1         | key1
    }

    def "return null as the key object if the target object is null"() {
        expect:
        accessor.getKey(null) == null
    }

    def "return null as the key object if the given map does not contain it"() {
        setup:
        def map = [] as HashMap
        map.put("d", "whatever value")

        expect:
        accessor.getKey(map) == null
    }

    def "toString should make clear it's a map accessor"() {
        expect:
        accessor.toString() == "map key {b}"
    }

    def "unset should be null-safe"() {
        when:
        accessor.unset null

        then:
        notThrown Throwable
    }

    def "set should be null-safe"() {
        when:
        accessor.set null, 'foo'

        then:
        notThrown Throwable
    }

    def "get should be null-safe"() {
        when:
        accessor.get null

        then:
        notThrown Throwable
    }

    def "equals should work as expected"() {
        given:
        def accessor = new MapEntryAccessor("a")

        expect:
        accessor.equals(otherAccessor) == expectedResult

        and: "make sure the same instance is always equal"
        accessor.equals(accessor)

        where:
        otherAccessor             || expectedResult
        null                      || false
        "foo"                     || false
        new MapEntryAccessor("b") || false
        new MapEntryAccessor("a") || true
    }

    def "hashCode should equal hashCode of referenceKey"() {
        setup:
        def referenceKey = "foo"

        expect:
        new MapEntryAccessor(referenceKey).hashCode() == referenceKey.hashCode()
    }
}
