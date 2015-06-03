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

package de.danielbechler.diff.node

import de.danielbechler.diff.access.Accessor
import de.danielbechler.diff.introspection.PropertyAccessor
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification

class DiffNodeFieldAnnotationsTest extends Specification {

	def 'getFieldAnnotation(): returns null if not PropertyAwareAccessor'() {
		given:
            def accessor = Mock(Accessor) {
                getElementSelector() >> new BeanPropertyElementSelector('f')
            }
		    def diffNode = new DiffNode(null, accessor, A)
		expect:
			diffNode.fieldAnnotations.size() == 0
		    diffNode.getFieldAnnotation(SomeFieldAnnotation) == null
	}

	def 'getFieldAnnotation(): class has the field and annotated'() {
		given:
			def accessor = new PropertyAccessor("f", A.getMethod("getF"), A.getMethod("setF", String))
			def diffNode = new DiffNode(null, accessor, A)
		expect:
			diffNode.fieldAnnotations.size() == 1
			diffNode.getFieldAnnotation(SomeFieldAnnotation) != null
			diffNode.getFieldAnnotation(SomeFieldAnnotation).annotationType() == SomeFieldAnnotation
	}

	def 'getFieldAnnotation(): class does not have the field, or different name'() {
		given:
            def accessor = new PropertyAccessor("F", ADiffName.getMethod("getF"), null)
            def diffNode = new DiffNode(null, accessor, ADiffName)
		expect:
			diffNode.fieldAnnotations.size() == 0
            diffNode.getFieldAnnotation(SomeFieldAnnotation) == null
	}

	def 'getFieldAnnotation(): inheritance'() {
		given:
		def accessor = new PropertyAccessor("f", AB.getMethod("getF"), AB.getMethod("setF", String))
		def diffNode = new DiffNode(null, accessor, AB)
		expect:
		diffNode.fieldAnnotations.size() == 1
		diffNode.getFieldAnnotation(SomeFieldAnnotation) != null
		diffNode.getFieldAnnotation(SomeFieldAnnotation).annotationType() == SomeFieldAnnotation
	}

	def 'getFieldAnnotation(): inheritance, overridden getter'() {
		given:
		def accessor = new PropertyAccessor("f", ABGetter.getMethod("getF"), ABGetter.getMethod("setF", String))
		def diffNode = new DiffNode(null, accessor, ABGetter)
		expect:
		diffNode.fieldAnnotations.size() == 1
		diffNode.getFieldAnnotation(SomeFieldAnnotation) != null
		diffNode.getFieldAnnotation(SomeFieldAnnotation).annotationType() == SomeFieldAnnotation
	}

	def 'getFieldAnnotation(): inheritance, not annotated'() {
		given:
		def accessor = new PropertyAccessor("f", NAB.getMethod("getF"), NAB.getMethod("setF", String))
		def diffNode = new DiffNode(null, accessor, NAB)
		expect:
		diffNode.fieldAnnotations.size() == 0
		diffNode.getFieldAnnotation(SomeFieldAnnotation) == null
	}

	def 'getFieldAnnotation(): inheritance, overridden getter, not annotated'() {
		given:
		def accessor = new PropertyAccessor("f", NABGetter.getMethod("getF"), NABGetter.getMethod("setF", String))
		def diffNode = new DiffNode(null, accessor, NABGetter)
		expect:
		diffNode.fieldAnnotations.size() == 0
		diffNode.getFieldAnnotation(SomeFieldAnnotation) == null
	}

	public static class A {
		@SomeFieldAnnotation
		String f;
	}

	public static class NA {
		String f;
	}

    public static class ADiffName {
        public String getF() {
            return null;
        }
    }

	public static class AB extends A {
	}

	public static class ABGetter extends A {
		@Override
		public String getF() {
			return null;
		}
	}

	public static class NAB extends NA {
	}

	public static class NABGetter extends NA {
		@Override
		public String getF() {
			return null;
		}
	}

}
