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

package de.danielbechler.diff.introspection;

import de.danielbechler.diff.mock.ObjectWithAnnotatedProperty;
import de.danielbechler.diff.mock.ObjectWithHashCodeAndEquals;
import de.danielbechler.diff.mock.ObjectWithStringAndUnsupportedWriteMethod;
import de.danielbechler.diff.selector.BeanPropertyElementSelector;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import static java.util.Arrays.asList;
import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Daniel Bechler
 */
public class BeanPropertyAccessorShould
{
	private BeanPropertyAccessor accessor;
	private ObjectWithHashCodeAndEquals item;
//	private Method readMethod;

	@BeforeMethod
	public void setUp() throws Exception
	{
		accessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals.class)
				.property("value", String.class)
				.readOnly(false)
				.build();
//		readMethod = ObjectWithHashCodeAndEquals.class.getMethod("getValue");
//		final Method writeMethod = ObjectWithHashCodeAndEquals.class.getMethod("setValue", String.class);
//		accessor = new PropertyAccessor("value", readMethod, writeMethod);
		item = new ObjectWithHashCodeAndEquals("foo");
	}

	@Test
	public void assign_new_value_if_property_is_writable() throws Exception
	{
		assertThat(item.getValue()).isNull();

		accessor.set(item, "bar");

		assertThat(item.getValue()).isEqualTo("bar");
	}

	@Test(expectedExceptions = BeanPropertyWriteException.class)
	public void fail_on_set_if_no_setter_exists() throws NoSuchMethodException
	{
		final ObjectWithStringAndUnsupportedWriteMethod target = new ObjectWithStringAndUnsupportedWriteMethod("foo");
		final Method readMethod = target.getClass().getMethod("getValue");
		final Method writeMethod = target.getClass().getMethod("setValue", String.class);
		accessor = new BeanPropertyAccessor("value", readMethod, writeMethod);
		accessor.set(target, "bar");
	}

	@Test
	public void assign_nothing_if_no_write_method_is_available() throws Exception
	{
		accessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithHashCodeAndEquals.class)
				.property("value", String.class)
				.readOnly(true)
				.build();

		accessor.set(item, "bar");

		assertThat(item.getValue()).isNull();
	}

	@Test
	public void retrieve_correct_value() throws Exception
	{
		item.setValue("bar");

		final String value = (String) accessor.get(item);

		assertThat(value).isEqualTo("bar");
	}

	@Test
	public void retrieve_null_if_target_is_null()
	{
		assertThat(accessor.get(null)).isNull();
	}

	@Test
	public void assign_nothing_if_target_is_null() throws Exception
	{
		accessor.set(null, "bar"); // just to make sure no exception is thrown
	}

	@Test(expectedExceptions = BeanPropertyReadException.class)
	public void fail_if_target_does_not_have_expected_read_method() throws NoSuchMethodException
	{
		accessor.get(new Object());
	}

	@Test(expectedExceptions = BeanPropertyWriteException.class)
	public void fail_if_target_does_not_have_expected_write_method() throws NoSuchMethodException
	{
		accessor.set(new Object(), "foo");
	}

	@Test
	public void unset_property_value() throws Exception
	{
		item.setValue("bar");

		accessor.unset(item);

		assertThat(item.getValue()).isNull();
	}

	@SuppressWarnings({"unchecked"})
	@Test
	public void return_property_value_type() throws Exception
	{
		assertThat((Class<String>) accessor.getType()).isEqualTo(String.class);
	}

	@Test
	public void return_proper_path_element() throws Exception
	{
		assertThat(accessor.getElementSelector()).isEqualTo(new BeanPropertyElementSelector("value"));
	}

	@Test
	public void returns_proper_property_name() throws Exception
	{
		assertThat(accessor.getPropertyName()).isEqualTo("value");
	}

	@Test
	public void includes_accessor_type_in_string_representation()
	{
		assertThat(accessor.toString()).startsWith("property ");
	}

	@Test
	public void returns_annotations_of_property_getter() throws Exception
	{
		accessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty.class)
				.property("value", String.class)
				.readOnly(false)
				.build();
		final Annotation[] expectedAnnotations = ObjectWithAnnotatedProperty.class.getMethod("getValue")
				.getAnnotations();
		assertThat(expectedAnnotations).hasSize(2);
		assertThat(accessor.getReadMethodAnnotations()).containsAll(asList(expectedAnnotations));
	}

	@Test
	public void returns_specific_annotation_of_property_getter() throws Exception
	{
		accessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty.class)
				.property("value", String.class)
				.readOnly(false)
				.build();
		final ObjectDiffProperty expectedAnnotation = ObjectWithAnnotatedProperty.class.getMethod("getValue")
				.getAnnotation(ObjectDiffProperty.class);
		assertThat(expectedAnnotation).isNotNull();
		final ObjectDiffProperty annotation = accessor.getReadMethodAnnotation(ObjectDiffProperty.class);
		assertThat(annotation).isEqualTo(expectedAnnotation);
	}

	@Test
	public void returns_null_if_specific_annotation_of_property_getter_does_not_exist() throws Exception
	{
		accessor = BeanPropertyAccessorBuilder.forPropertyOf(ObjectWithAnnotatedProperty.class)
				.property("value", String.class)
				.readOnly(false)
				.build();
		assertThat(accessor.getReadMethodAnnotation(Override.class)).isNull();
	}
}
