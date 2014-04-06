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

package de.danielbechler.diff.issues.issue3

import de.danielbechler.diff.ObjectDifferBuilder
import spock.lang.Ignore
import spock.lang.Specification

/**
 * Created by dbechler.
 */
class IgnoreTest extends Specification {

    @Ignore
    def "ignoring by property name"() {
        given:
        def base = new FooContainer("foo-base", "bar-base", "test-base")
        def working = new FooContainer("foo-working", "bar-working", "test-working")

        and: "some properties are excluded by name"
        def builder = ObjectDifferBuilder.startBuilding()
        builder.configure().inclusion().exclude().propertyName('foo').propertyName('bar')
        def objectDiffer = builder.build()

        when:
        def result = objectDiffer.compare(working, base)

        then: "excluded properties should not have been diffed"
        result.childCount() == 1
        result.getChild("barContainer") != null
        result.getChild("barContainer").childCount() == 1
        result.getChild("barContainer").getChild("test") != null
    }

    class FooContainer {
        def foo
        def barContainer

        FooContainer(foo, bar, test) {
            this.foo = foo
            this.barContainer = new BarContainer(bar, test)
        }
    }

    class BarContainer {
        def bar
        def test

        BarContainer(bar, test) {
            this.bar = bar
            this.test = test
        }
    }
}
