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

package de.danielbechler.diff.example

import de.danielbechler.diff.Configuration
import de.danielbechler.diff.ObjectDifferFactory
import de.danielbechler.diff.annotation.ObjectDiffEqualsOnlyType
import de.danielbechler.diff.annotation.ObjectDiffProperty
import de.danielbechler.diff.path.PropertyPath
import spock.lang.Specification

import static de.danielbechler.diff.node.Node.State.UNTOUCHED

class Example2Specification extends Specification
{
  def "Comparing an object via equalsOnlyValueProvider (via property annotation)"()
  {
    given:
    def base = new Company()
    base.manager = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Walt", "White")

    and:
    def working = new Company()
    working.manager = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Heisenberg", "White")

    expect:
    ObjectDifferFactory.getInstance().compare(working, base).state == UNTOUCHED
  }

  def "Comparing an object via equalsOnlyValueProvider (via type annotation)"()
  {
    given:
    def base = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Walt", "White")
    def working = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Heisenberg", "White")

    expect:
    ObjectDifferFactory.getInstance().compare(working, base).state == UNTOUCHED
  }

  def "Comparing an object via equalsOnlyValueProvider (via configuration using property path)"()
  {
    given:
    def configuration = new Configuration().withEqualsOnlyValueProviderMethod(PropertyPath.buildRootPath(), "presentationForDiffer")
    def base = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Walt", "White")
    def working = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Heisenberg", "White")

    expect:
    ObjectDifferFactory.getInstance(configuration).compare(working, base).state == UNTOUCHED
  }

  def "Comparing an object via equalsOnlyValueProvider (via configuration using type)"()
  {
    given:
    def configuration = new Configuration().withEqualsOnlyValueProviderMethod(Person.class, "presentationForDiffer")
    def base = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Walt", "White")
    def working = new PersonWithEqualsOnlyTypeAnnotation("Walter", "Heisenberg", "White")

    expect:
    ObjectDifferFactory.getInstance(configuration).compare(working, base).state == UNTOUCHED
  }


  public static class Company
  {
    private Person manager;

    @ObjectDiffProperty(equalsOnly = true, equalsOnlyValueProviderMethod = "presentationForDiffer")
    public Person getManager()
    {
      return manager
    }

    void setManager(final Person manager)
    {
      this.manager = manager
    }
  }

  public static class Person
  {
    private String firstname
    private String lastname
    private String nickname

    public Person(String firstname, String nickname, String lastname)
    {
      this.firstname = firstname
      this.nickname = nickname
      this.lastname = lastname
    }

    public String presentationForDiffer()
    {
      return firstname + " " + lastname
    }

    boolean equals(final o)
    {
      return false
    }
  }

  @ObjectDiffEqualsOnlyType(valueProviderMethod = "presentationForDiffer")
  public static class PersonWithEqualsOnlyTypeAnnotation extends Person
  {
    public PersonWithEqualsOnlyTypeAnnotation(final String firstname, final String nickname, final String lastname)
    {
      super(firstname, nickname, lastname)
    }
  }
}
