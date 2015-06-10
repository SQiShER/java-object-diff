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

package de.danielbechler.diff.identity

import de.danielbechler.diff.ObjectDifferBuilder
import de.danielbechler.diff.comparison.IdentityStrategy
import de.danielbechler.diff.node.DiffNode
import de.danielbechler.diff.node.Visit
import de.danielbechler.diff.path.NodePath
import de.danielbechler.diff.selector.CollectionItemElementSelector
import spock.lang.Specification

class IdentityStrategyIT extends Specification {

    List<A> list1 = [
            new A(id: "Id1", code: "Code1"),
            new A(id: "Id2", code: "Code2"),
            new A(id: "Id3", code: "Code3")
    ]
    List<A> list2 = [
            new A(id: "Id1", code: "Code1"),
            new A(id: "Id2", code: "Code2"),
            new A(id: "Id3", code: "Code3")
    ]
    List<A> list2b = [
            new A(id: "Id2", code: "Code2"),
            new A(id: "Id3", code: "Code3"),
            new A(id: "Id1", code: "Code1")
    ]
    List<A> list3 = [
            new A(id: "Id1", code: "Code1"),
            new A(id: "Id2", code: "newCode"),
            new A(id: "newId", code: "Code2")
    ]

//    def 'Test default equals SAME'() {
//        when:
//        def diffNode = ObjectDifferBuilder.startBuilding()
//                .build().compare(list2, list1)
//        then:
//        diffNode.untouched
//    }
//
//    def 'Test default equals SAME B'() {
//        when:
//        def diffNode = ObjectDifferBuilder.startBuilding()
//                .build().compare(list2b, list1)
//        then:
//        diffNode.untouched
//    }
//
//    def 'Test default equals CHANGED'() {
//        when:
//        def diffNode = ObjectDifferBuilder.startBuilding()
//                .build().compare(list3, list1)
//        then:
//        diffNode.changed
//        diffNode.getChild(new CollectionItemElementSelector(new A(id: "Id1"))) == null
//        diffNode.getChild(new CollectionItemElementSelector(new A(id: "Id2"))).changed
//        diffNode.getChild(new CollectionItemElementSelector(new A(id: "newId"))).added
//        diffNode.getChild(new CollectionItemElementSelector(new A(id: "Id3"))).removed
//    }
//
//    def 'Test field CODE equals SAME'() {
//        when:
//        def diffNode = ObjectDifferBuilder.startBuilding()
//                .comparison().ofType(A).toUseEqualsMethodOfValueProvidedByMethod("getCode").and()
//                .build().compare(list2, list1)
//        then:
//        diffNode.state == DiffNode.State.UNTOUCHED
//    }
//
//    def 'Test field CODE equals SAME B'() {
//        when:
//        def diffNode = ObjectDifferBuilder.startBuilding()
//                .identity().ofType(A).toUse(new CodeIdentity()).and()
//                .build().compare(list2b, list1)
//        then:
//        diffNode.state == DiffNode.State.UNTOUCHED
//    }

    def 'Test field CODE equals equals CHANGED'() {
        when:
        def codeStrategy = new CodeIdentity();
        def diffNode = ObjectDifferBuilder.startBuilding()
                .comparison().ofCollectionItems(NodePath.withRoot()) // TODO configuration shouldn't be like this!
                .toUse(codeStrategy).and()
                .build().compare(list3, list1)
        then:
        diffNode.state == DiffNode.State.CHANGED
        diffNode.getChild(new CollectionItemElementSelector(new A(code: "Code1"), codeStrategy)) == null
        diffNode.getChild(new CollectionItemElementSelector(new A(code: "newCode"), codeStrategy)).added
        diffNode.getChild(new CollectionItemElementSelector(new A(code: "Code2"), codeStrategy)).changed
        diffNode.getChild(new CollectionItemElementSelector(new A(code: "Code3"), codeStrategy)).removed
    }

    private void print(final DiffNode diffNode, final Object working,
                       final Object base) {
        diffNode.visit(new DiffNode.Visitor() {
            @Override
            void node(final DiffNode node, final Visit visit) {
                System.out.println("" + node.getPath() + " " + node.getState() + " "
                        + node.canonicalGet(base) + " => " + node.canonicalGet(working))
            }
        })
    }

    public static class A {
        String id;
        String code;

        String getCode() {
            return code
        }

        @Override
        boolean equals(final o) {
            if (this.is(o)) return true
            if (!(o instanceof A)) return false

            A a = (A) o

            if (!Objects.equals(id, a.id)) return false

            return true
        }

        @Override
        int hashCode() {
            return (id != null ? id.hashCode() : 0)
        }
    }

    public static class CodeIdentity implements IdentityStrategy {
        @Override
        boolean equals(final Object working, final Object base) {
            return Objects.equals(((A) working).getCode(), ((A) base).getCode());
        }
    }
}
