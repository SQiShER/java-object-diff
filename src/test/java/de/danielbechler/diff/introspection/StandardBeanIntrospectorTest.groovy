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
import de.danielbechler.diff.mock.ObjectWithInheritedPropertyAnnotation
import de.danielbechler.diff.mock.ObjectWithPropertyAnnotations
import de.danielbechler.diff.mock.ObjectWithString
import de.danielbechler.diff.selector.BeanPropertyElementSelector
import spock.lang.Specification

import java.beans.BeanInfo
import java.beans.IntrospectionException

/**
 * @author Daniel Bechler
 */
public class StandardBeanIntrospectorTest extends Specification {
	StandardBeanIntrospector introspector = new StandardBeanIntrospector()

//	@Test(enabled = false)
//	public void testIntrospectWithEqualsOnlyPropertyType() throws Exception {
//		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(ObjectWithEqualsOnlyPropertyType.class);
//		assertThat(accessors.iterator().hasNext(), is(true));
////		final PropertyAwareAccessor propertyAwareAccessor = accessors.iterator().next();
////		assertThat(propertyAwareAccessor.getComparisonStrategy(), instanceOf(EqualsOnlyComparisonStrategy.class));
//	}
//
//	@Test
//	public void testIntrospectWithEqualsOnlyPropertyTypeAndValueProviderMethod() throws Exception {
//		final Object object = new Object()
//		{
//			public ObjectWithObjectDiffEqualsOnlyTypeAnnotationAndValueProviderMethod getValue() {
//				return null;
//			}
//		};
//
//		final Iterable<PropertyAwareAccessor> accessors = introspector.introspect(object.getClass());
//		assertThat(accessors.iterator().hasNext(), is(true));
//
//		final PropertyAwareAccessor propertyAwareAccessor = accessors.iterator().next();
//
////		final ComparisonStrategy comparisonStrategy = propertyAwareAccessor.getComparisonStrategy();
////		assertThat(comparisonStrategy, is(instanceOf(EqualsOnlyComparisonStrategy.class)));
//
////		final EqualsOnlyComparisonStrategy equalsOnlyComparisonStrategy = (EqualsOnlyComparisonStrategy) comparisonStrategy;
////		assertThat(equalsOnlyComparisonStrategy.getEqualsValueProviderMethod(), is(IsEqual.equalTo("foo")));
//	}

	class ObjectWithIgnoredProperty {
		@ObjectDiffProperty(excluded = true)
		def getProperty() {
		}
	}

	def 'excluded property'() {
		when:
		  PropertyAwareAccessor propertyAccessor = introspector.introspect(ObjectWithIgnoredProperty).find({
			  it.propertyName == 'property' ? it : null
		  }) as PropertyAwareAccessor
		then:
		  propertyAccessor.isExcluded()
	}

	def 'Introspect With Property Annotations'() {
		when:
		  Iterable<PropertyAwareAccessor> accessors = introspector.introspect(ObjectWithPropertyAnnotations.class);

		then:
		  for (final PropertyAwareAccessor accessor : accessors) {
			  if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("ignored"))) {
				  assert accessor.isExcluded()
			  } else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("categorized"))) {
				  assert accessor.getCategories().size() == 1
				  assert accessor.getCategories().containsAll(['foo'])
			  } else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("item"))) {
				  assert accessor.isExcluded() == false
				  assert accessor.getCategories().isEmpty()
			  } else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("key"))) {
				  // no op
			  } else if (accessor.getElementSelector().equals(new BeanPropertyElementSelector("value"))) {
				  // no op
			  } else {
				  assert false: "Unexpected accessor: " + accessor.getElementSelector()
			  }
		  }
	}

	def IntrospectWithInheritedPropertyAnnotations() {
		when:
		  PropertyAwareAccessor accessor = introspector.introspect(ObjectWithInheritedPropertyAnnotation).first();
		then:
		  accessor.getElementSelector() == new BeanPropertyElementSelector("value")
		  accessor.isExcluded()
	}

	def IntrospectWithNullType() {
		when:
		  introspector.introspect(null);
		then:
		  thrown(IllegalArgumentException)
	}

	def IntrospectWithSimulatedIntrospectionException() {
		given:
		  introspector = new StandardBeanIntrospector() {
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
}
