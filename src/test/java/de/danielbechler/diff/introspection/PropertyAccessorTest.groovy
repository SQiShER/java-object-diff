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
public class PropertyAccessorTest extends Specification {
	PropertyAccessor propertyAccessor
	ObjectWithHashCodeAndEquals item

	def 'setup'() {
		propertyAccessor = PropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals)
				.property("value", String)
				.readOnly(false)
				.build()
		item = new ObjectWithHashCodeAndEquals("foo")
	}

	def 'assign new value if property is writable'() {
		given:
		  item.value = null
		when:
		  propertyAccessor.set(item, "bar")
		then:
		  item.value == 'bar'
	}

	def 'fail on set if no setter exists'() {
		setup:
		  ObjectWithStringAndUnsupportedWriteMethod target = new ObjectWithStringAndUnsupportedWriteMethod("foo")
		  Method readMethod = target.getClass().getMethod("getValue")
		  Method writeMethod = target.getClass().getMethod("setValue", String)
		  propertyAccessor = new PropertyAccessor("value", null, readMethod, writeMethod)
		when:
		  propertyAccessor.set(target, "bar")
		then:
		  thrown(PropertyWriteException)
	}

	def 'assign nothing if no write method is available'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals)
				  .property("value", String)
				  .readOnly(true)
				  .build()
		when:
		  propertyAccessor.set(item, "bar")
		then:
		  item.value == null
	}

	def 'retrieve correct value'() {
		given:
		  item.value = "bar"
		when:
		  final String value = (String) propertyAccessor.get(item)
		then:
		  value == "bar"
	}

	def 'retrieve null if target is null'() {
		expect:
		  propertyAccessor.get(null) == null
	}

	def 'assign nothing if target is null'() {
		expect:
		  propertyAccessor.set(null, "bar"); // just to make sure no exception is thrown
	}

	def 'fail if target does not have expected read method'() {
		when:
		  propertyAccessor.get(new Object())
		then:
		  thrown(PropertyReadException)
	}

	def 'fail if target does not have expected write method'() {
		when:
		  propertyAccessor.set(new Object(), "foo")
		then:
		  thrown(PropertyWriteException)
	}

	def 'unset property value'() {
		given:
		  item.value = "bar"
		when:
		  propertyAccessor.unset(item)
		then:
		  item.value == null
	}

	def 'return property value type'() {
		expect:
		  propertyAccessor.type == String
	}

	def 'return proper path element'() {
		expect:
		  propertyAccessor.elementSelector == new BeanPropertyElementSelector("value")
	}

	def 'returns proper property name'() {
		expect:
		  propertyAccessor.propertyName == "value"
	}

	def 'toString returns debug friendly string'() {
		expect:
		  propertyAccessor.toString() == 'PropertyAccessor{propertyName=\'value\'' +
				  ', type=java.lang.String' +
				  ', source=de.danielbechler.diff.mock.ObjectWithHashCodeAndEquals' +
				  ', hasWriteMethod=true}'
	}

	def 'returns annotations of property getter'() {
		setup:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty)
				  .property("value", String)
				  .readOnly(false)
				  .build()
		  final Annotation[] expectedAnnotations = ObjectWithAnnotatedProperty.class.getMethod("getValue").annotations
		expect:
		  expectedAnnotations.length == 2
		and:
		  propertyAccessor.readMethodAnnotations.containsAll(expectedAnnotations)
	}

	def 'returns specific annotation of property getter'() {
		setup:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty)
				  .property("value", String)
				  .readOnly(false)
				  .build()
		  ObjectDiffProperty expectedAnnotation = ObjectWithAnnotatedProperty.class.getMethod("getValue").getAnnotation(ObjectDiffProperty)
		expect:
		  expectedAnnotation != null
		and:
		  propertyAccessor.getReadMethodAnnotation(ObjectDiffProperty) == expectedAnnotation
	}

	def 'returns null if specific annotation of property getter does not exist'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty.class)
				  .property("value", String.class)
				  .readOnly(false)
				  .build()
		expect:
		  propertyAccessor.getReadMethodAnnotation(Override.class) == null
	}

	def 'getCategoriesFromAnnotation returns empty set when annotation is absent'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(AnnotatedType)
				  .property('unannotatedValue', String)
				  .readOnly(true)
				  .build()
		expect:
		  propertyAccessor.categoriesFromAnnotation == [] as Set
	}

	def 'getCategoriesFromAnnotation returns set of categories from annotation'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(AnnotatedType)
				  .property('value', String)
				  .readOnly(true)
				  .build()
		expect:
		  propertyAccessor.categoriesFromAnnotation == ['A', 'B', 'C'] as Set
	}

	class AnnotatedType {
		@ObjectDiffProperty(excluded = true, categories = ['A', 'B', 'C'])
		public String getValue() {
			return 'foo'
		}

		public String getUnannotatedValue() {
			return 'foo'
		}
	}

	def 'isExcludedByAnnotation returns value from annotation'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(AnnotatedType)
				  .property('value', String)
				  .readOnly(true)
				  .build()
		expect:
		  propertyAccessor.excludedByAnnotation == true
	}

	def 'isExcludedByAnnotation returns false when annotation is absent'() {
		given:
		  propertyAccessor = PropertyAccessorBuilder.forPropertyOf(AnnotatedType)
				  .property('unannotatedValue', String)
				  .readOnly(true)
				  .build()
		expect:
		  propertyAccessor.excludedByAnnotation == false
	}
}
