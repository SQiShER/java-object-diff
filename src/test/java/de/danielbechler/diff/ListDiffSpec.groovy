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

package de.danielbechler.diff

import de.danielbechler.diff.collection.CollectionItemElementSelector
import de.danielbechler.diff.visitor.PrintingVisitor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * @author Daniel Bechler
 */
class ListDiffSpec extends Specification {
    def objectDiffer;

    def setup() {
        objectDiffer = ObjectDifferBuilder.buildDefault();
    }

    @Ignore
    def "detects position switch"() {
        DiffNode node;

        when:
        node = objectDiffer.compare(working, base);

        then:
        node.visit(new PrintingVisitor(working, base));
        node.getChild(new CollectionItemElementSelector("a")).getState() == DiffNode.State.CHANGED;
        node.getChild(new CollectionItemElementSelector("b")).getState() == DiffNode.State.CHANGED;

        where:
        base       | working
        ["a", "b"] | ["b", "a"]
    }
}
