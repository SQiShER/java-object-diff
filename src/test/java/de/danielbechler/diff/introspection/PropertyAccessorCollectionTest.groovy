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

package de.danielbechler.diff.introspection

import de.danielbechler.diff.mock.ObjectWithCollection
import spock.lang.Specification

import java.lang.reflect.Method

import static java.util.Collections.singletonList

/**
 * @author Daniel Bechler
 */
public class PropertyAccessorCollectionTest extends Specification {

	PropertyAccessor beanPropertyAccessor

	def setup() {
		beanPropertyAccessor = createCollectionPropertyAccessor(false)
	}

	def 'replace content of mutable target collection if no setter is available'() {
		given:
		  def target = new ObjectWithCollection([])
		  def initialCollectionInstance = target.collection
		  target.collection.add('bar')
		  beanPropertyAccessor = createCollectionPropertyAccessor(true)
		when:
		  beanPropertyAccessor.set(target, ['foo'])
		then:
		  target.collection == ['foo']
		  target.collection.is initialCollectionInstance
	}

	def 'assign nothing if target collection is null and no write method is available'() {
		given:
		  def target = new ObjectWithCollection(null)
		and:
		  beanPropertyAccessor = createCollectionPropertyAccessor(true)
		when:
		  beanPropertyAccessor.set(target, singletonList("foo"))
		then:
		  target.collection == null
	}

	def 'assign nothing if target collection is immutable and no write method is available'() {
		given:
		  def target = new ObjectWithCollection([].asImmutable())
		  beanPropertyAccessor = createCollectionPropertyAccessor(true)
		when:
		  beanPropertyAccessor.set(target, singletonList("foo"))
		then:
		  target.collection.isEmpty()
	}

	def PropertyAccessor createCollectionPropertyAccessor(boolean readOnly) {
		Method readMethod = ObjectWithCollection.getMethod("getCollection")
		Method writeMethod = readOnly ? null : ObjectWithCollection.getMethod("setCollection", Collection)
		return new PropertyAccessor("collection", null, readMethod, writeMethod)
	}

}
