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

package de.danielbechler.diff.access

import de.danielbechler.diff.mock.ObjectWithIdentityAndValue
import de.danielbechler.diff.selector.CollectionItemElementSelector
import spock.lang.Specification
import spock.lang.Unroll

public class CollectionItemAccessorSpec extends Specification {

	private static final def ANY_REFERENCE_ITEM = new Object()

	def "construction: should work with reference item"() {
		when:
		  new CollectionItemAccessor(new Object()) != null
		then:
		  notThrown(Throwable)
	}

	def "construction: should work with 'null' reference item"() {
		when:
		  new CollectionItemAccessor(null)
		then:
		  notThrown(Throwable)
	}

	def "get: should return item equal to reference item from collection"() {
		given:
		  def accessor = new CollectionItemAccessor(new ObjectWithIdentityAndValue("1"))
		and:
		  def collection = [
				  new ObjectWithIdentityAndValue("1", "foo")
		  ]
		when:
		  ObjectWithIdentityAndValue item = accessor.get(collection) as ObjectWithIdentityAndValue
		then:
		  item.id == '1'
		  item.value == 'foo'
	}

	def "get: should return null when no item in the collection matches the reference item"() {
		given:
		  def referenceItemForNonExistingItem = new ObjectWithIdentityAndValue("1")
		  def accessor = new CollectionItemAccessor(referenceItemForNonExistingItem)
		expect:
		  accessor.get([]) == null
	}

	def "get: should return null when reference item is null"() {
		given:
		  def referenceItem = null
		  def accessor = new CollectionItemAccessor(referenceItem)
		expect:
		  accessor.get(['some-item']) == null
	}

	def 'get: should fail with exception if target object is not a collection'() {
		given:
		  def accessor = new CollectionItemAccessor(ANY_REFERENCE_ITEM)
		  def notACollection = new Object()
		when:
		  accessor.get(notACollection)
		then:
		  thrown(IllegalArgumentException)
	}

	def 'set: should fail with exception if target object is not a collection'() {
		given:
		  def accessor = new CollectionItemAccessor(ANY_REFERENCE_ITEM)
		  def notACollection = new Object()
		when:
		  accessor.get(notACollection)
		then:
		  thrown(IllegalArgumentException)
	}

	def 'set: should insert non-existing item into collection'() throws Exception {
		given:
		  def collection = []
		  def accessor = new CollectionItemAccessor(new ObjectWithIdentityAndValue("foo"))
		when:
		  accessor.set(collection, new ObjectWithIdentityAndValue("foo", 'bar'))
		then:
		  collection.find { it.id == 'foo' && it.value == 'bar' } != null
	}

	def 'set: should do nothing if target object is null'() {
		given:
		  def accessor = new CollectionItemAccessor('foo')
		when:
		  accessor.set(null, 'bar');
		then:
		  notThrown(Throwable)
	}

	@Unroll
	def 'set: should replace matching item in collection (#collectionType)'() {
		given:
		  def collection = collectionType.newInstance()
		  def collectionItem = new ObjectWithIdentityAndValue("foo", 'bar')
		  collection.add(collectionItem)
		and:
		  def referenceItem = new ObjectWithIdentityAndValue("foo")
		  def accessor = new CollectionItemAccessor(referenceItem)
		when:
		  accessor.set(collection, 'new value')
		then:
		  collection.size() == 1
		  collection.contains 'new value'
		where:
		  collectionType << [ArrayList, HashSet]
	}

	@Unroll
	def 'unset: should remove matching item from collection (#collectionType)'() {
		given:
		  def collection = collectionType.newInstance()
		  def collectionItem = new ObjectWithIdentityAndValue("foo", 'bar')
		  collection.add(collectionItem)
		and:
		  def referenceItem = new ObjectWithIdentityAndValue("foo")
		  def accessor = new CollectionItemAccessor(referenceItem)
		when:
		  accessor.unset collection
		then:
		  collection.isEmpty()
		where:
		  collectionType << [ArrayList, HashSet]
	}

	@Unroll
	def 'unset: should not fail if reference item is not in collection (#collectionType)'() {
		given:
		  def emptyCollection = collectionType.newInstance()
		and:
		  def referenceItem = new ObjectWithIdentityAndValue("foo")
		  def accessor = new CollectionItemAccessor(referenceItem)
		when:
		  accessor.unset emptyCollection
		then:
		  emptyCollection.isEmpty()
		where:
		  collectionType << [ArrayList, HashSet]
	}

	def 'unset: should fail with exception if target object is not a collection'() {
		given:
		  def accessor = new CollectionItemAccessor(ANY_REFERENCE_ITEM)
		  def notACollection = new Object()
		when:
		  accessor.unset(notACollection);
		then:
		  thrown(IllegalArgumentException)
	}

	@Unroll
	def 'getType: should return type of reference item (#expectedType)'() {
		given:
		  def accessor = new CollectionItemAccessor(referenceItem)
		expect:
		  accessor.type == expectedType
		where:
		  referenceItem || expectedType
		  null          || null
		  'foo'         || String
		  new Date()    || Date
	}

	def 'toString: should return text starting with "collection item"'() {
		given:
		  def accessor = new CollectionItemAccessor(ANY_REFERENCE_ITEM)
		expect:
		  accessor.toString() =~ /^collection\sitem/
	}

	def 'getElementSelector: should return proper element selector'() {
		given:
		  def referenceItem = 'foo'
		  def accessor = new CollectionItemAccessor(referenceItem)
		expect:
		  accessor.elementSelector == new CollectionItemElementSelector(referenceItem)
	}
}
