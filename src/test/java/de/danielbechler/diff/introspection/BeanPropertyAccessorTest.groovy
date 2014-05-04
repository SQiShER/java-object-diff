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

import de.danielbechler.diff.mock.ObjectWithAnnotatedProperty
import de.danielbechler.diff.mock.ObjectWithHashCodeAndEquals
import de.danielbechler.diff.mock.ObjectWithStringAndUnsupportedWriteMethod
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification

import java.lang.annotation.Annotation
import java.lang.reflect.Method

/**
 * @author Daniel Bechler
 */
public class BeanPropertyAccessorTest extends Specification {
	BeanPropertyAccessor beanPropertyAccessor
	ObjectWithHashCodeAndEquals item

	def 'setup'() {
		beanPropertyAccessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals)
				.property("value", String)
				.readOnly(false)
				.build()
		item = new ObjectWithHashCodeAndEquals("foo")
	}

	def 'assign new value if property is writable'() {
		given:
		  item.value = null
		when:
		  beanPropertyAccessor.set(item, "bar")
		then:
		  item.value == 'bar'
	}

	def 'fail on set if no setter exists'() {
		setup:
		  ObjectWithStringAndUnsupportedWriteMethod target = new ObjectWithStringAndUnsupportedWriteMethod("foo")
		  Method readMethod = target.getClass().getMethod("getValue")
		  Method writeMethod = target.getClass().getMethod("setValue", String)
		  beanPropertyAccessor = new BeanPropertyAccessor("value", readMethod, writeMethod)
		when:
		  beanPropertyAccessor.set(target, "bar")
		then:
		  thrown(BeanPropertyWriteException)
	}

	def 'assign nothing if no write method is available'() {
		given:
		  beanPropertyAccessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals)
				  .property("value", String)
				  .readOnly(true)
				  .build()
		when:
		  beanPropertyAccessor.set(item, "bar")
		then:
		  item.value == null
	}

	def 'retrieve correct value'() {
		given:
		  item.value = "bar"
		when:
		  final String value = (String) beanPropertyAccessor.get(item)
		then:
		  value == "bar"
	}

	def 'retrieve null if target is null'() {
		expect:
		  beanPropertyAccessor.get(null) == null
	}

	def 'assign nothing if target is null'() {
		expect:
		  beanPropertyAccessor.set(null, "bar"); // just to make sure no exception is thrown
	}

	def 'fail if target does not have expected read method'() {
		when:
		  beanPropertyAccessor.get(new Object())
		then:
		  thrown(BeanPropertyReadException)
	}

	def 'fail if target does not have expected write method'() {
		when:
		  beanPropertyAccessor.set(new Object(), "foo")
		then:
		  thrown(BeanPropertyWriteException)
	}

	def 'unset property value'() {
		given:
		  item.value = "bar"
		when:
		  beanPropertyAccessor.unset(item)
		then:
		  item.value == null
	}

	def 'return property value type'() {
		expect:
		  beanPropertyAccessor.type == String
	}

	def 'return proper path element'() {
		expect:
		  beanPropertyAccessor.elementSelector == new BeanPropertyElementSelector("value")
	}

	def 'returns proper property name'() {
		expect:
		  beanPropertyAccessor.propertyName == "value"
	}

	def 'includes accessor type in string representation'() {
		expect:
		  beanPropertyAccessor.toString().startsWith("property ")
	}

	def 'returns annotations of property getter'() {
		setup:
		  beanPropertyAccessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty)
				  .property("value", String)
				  .readOnly(false)
				  .build()
		  final Annotation[] expectedAnnotations = ObjectWithAnnotatedProperty.class.getMethod("getValue").annotations
		expect:
		  expectedAnnotations.length == 2
		and:
		  beanPropertyAccessor.readMethodAnnotations.containsAll(expectedAnnotations)
	}

	def 'returns specific annotation of property getter'() {
		setup:
		  beanPropertyAccessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty)
				  .property("value", String)
				  .readOnly(false)
				  .build()
		  ObjectDiffProperty expectedAnnotation = ObjectWithAnnotatedProperty.class.getMethod("getValue").getAnnotation(ObjectDiffProperty)
		expect:
		  expectedAnnotation != null
		and:
		  beanPropertyAccessor.getReadMethodAnnotation(ObjectDiffProperty) == expectedAnnotation
	}

	def 'returns null if specific annotation of property getter does not exist'() {
		given:
		  beanPropertyAccessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty.class)
				  .property("value", String.class)
				  .readOnly(false)
				  .build()
		expect:
		  beanPropertyAccessor.getReadMethodAnnotation(Override.class) == null
	}
}
