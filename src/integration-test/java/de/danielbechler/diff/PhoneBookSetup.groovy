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

package de.danielbechler.diff

import de.danielbechler.diff.annotation.ObjectDiffProperty
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

/**
 * Created by Daniel Bechler.
 */
public class PhoneBookSetup {

    static def getBase() {
        return new PhoneBook(
                name: "Jerry's Phone Book",
                revision: 1,
                contacts: [
                        new Contact(id: "elaine", name: "Elaine", number: "917-555-0186"),
                        new Contact(id: "george", name: "George", number: "917-555-0102"),
                        new Contact(id: "kramer", name: "Kramer", number: "917-555-3456")
                ])
    }

    static def getWorking() {
        return new PhoneBook(
                // Jerry decided to give his phone book a more formal title
                name: "Jerry Seinfeld's Phone Book",
                revision: 2,
                contacts: [
                        new Contact(id: "elaine", name: "Elaine", number: "917-555-0186"),

                        // George has a new nickname
                        new Contact(id: "george", name: "Koko", number: "917-555-0102"),

                        // Jerry always ended up at the Moviefone hotline until he realized Kramers new number actually ends with 5, not 6
                        new Contact(id: "kramer", name: "Kramer", number: "917-555-3455")
                ])
    }

    @EqualsAndHashCode
    @ToString(includePackage = false)
    public static class PhoneBook {
        def name
        def contacts = []
        def revision

        @SuppressWarnings("GroovyUnusedDeclaration")
        @ObjectDiffProperty(excluded = true)
        def getRevision() {
            return revision
        }

        void setRevision(revision) {
            this.revision = revision
        }
    }

    @EqualsAndHashCode(includes = ["id"])
    @ToString(includePackage = false)
    public static class Contact {
        def id
        def name
        def number

        @SuppressWarnings("GroovyUnusedDeclaration")
        @ObjectDiffProperty(categories = ['private'])
        def getNumber() {
            return number
        }

        void setNumber(number) {
            this.number = number
        }
    }
}
