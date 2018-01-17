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

import de.danielbechler.diff.access.PropertyAwareAccessor
import de.danielbechler.diff.mock.ObjectWithString
import spock.lang.Specification

import java.beans.BeanInfo
import java.beans.IntrospectionException

/**
 * @author Daniel Bechler
 */
public class GetterSetterIntrospectorTest extends Specification {

	def introspector = new GetterSetterIntrospector()

	private Map<String, PropertyAwareAccessor> introspect(Class<?> type) {
		introspector.introspect(type).accessors.collectEntries {
			accessor -> [accessor.propertyName, accessor]
		}
	}

	def 'should return proper accessor for property'() {
		when:
		def accessor = introspect(TypeWithOnlyOneProperty).get('value')
		then:
		accessor.propertyName == 'value'
		and:
		def target = new TypeWithOnlyOneProperty()
		accessor.get(target) == null
		and:
		accessor.set(target, 'bar')
		accessor.get(target) == 'bar'
		and:
		accessor.excludedByAnnotation == false
		and:
		accessor.categoriesFromAnnotation.isEmpty()
	}

	def 'should return PropertyAwareAccessors for each property of the given class'() {
		when:
		def accessors = introspect(TypeWithTwoProperties)
		then:
		accessors.size() == 2
		accessors.get('foo') != null
		accessors.get('bar') != null
	}

	def 'should apply categories of ObjectDiffProperty annotation to accessor'() {
		when:
		def accessor = introspect(TypeWithPropertyAnnotation).get('value')
		then:
		accessor.categoriesFromAnnotation.size() == 2
		accessor.categoriesFromAnnotation.containsAll(['category1', 'category2'])
	}

	def 'should apply exclusion of ObjectDiffProperty annotation to accessor'() {
		when:
		def accessor = introspect(TypeWithPropertyAnnotation).get('value')
		then:
		accessor.excludedByAnnotation == true
	}

	def 'should throw exception when invoked without type'() {
		when:
		introspector.introspect(null)
		then:
		thrown(IllegalArgumentException)
	}

	def 'should skip default class properties'() {
		expect:
		introspect(TypeWithNothingButDefaultProperties).isEmpty()
	}

	def 'should skip properties without getter'() {
		expect:
		introspect(TypeWithPropertyWithoutGetter).isEmpty()
	}

	def 'should wrap IntrospectionException with RuntimeException'() {
		given:
		introspector = new StandardIntrospector() {
			@Override
			protected BeanInfo getBeanInfo(final Class<?> type) throws IntrospectionException {
				throw new IntrospectionException(type.getCanonicalName());
			}
		};
		when:
		introspector.introspect(ObjectWithString.class);
		then:
		thrown(RuntimeException)
	}

	private class TypeWithNothingButDefaultProperties {
	}

	private class TypeWithPropertyWithoutGetter {
		private String value

		void setValue(String value) {
			this.value = value
		}
	}

	private class TypeWithPropertyAnnotation {
		private String value

		@ObjectDiffProperty(excluded = true, categories = ['category1', 'category2'])
		String getValue() {
			return value
		}

		void setValue(String value) {
			this.value = value
		}
	}

	private class TypeWithOnlyOneProperty {
		def value
	}

	private class TypeWithTwoProperties {
		def foo
		def bar
	}
}
