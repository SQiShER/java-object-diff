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

package de.danielbechler.diff.path;

import org.testng.annotations.*;

import static org.fest.assertions.api.Assertions.*;

/** @author Daniel Bechler */
public class PropertyPathBuilderTest
{
	@Test
	public void testWithRoot()
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withRoot()
													  .build();
		assertThat(propertyPath.getElements()).containsOnly(RootElement.getInstance());
	}

	@Test
	public void testWithElement()
	{
		final CollectionElement element = new CollectionElement("foo");
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withRoot()
													  .withElement(element)
													  .build();
		assertThat(propertyPath.getElements()).containsSequence(
				RootElement.getInstance(),
				element);
	}

	@Test
	public void testWithPropertyName()
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withRoot()
													  .withPropertyName("foo", "bar")
													  .build();
		assertThat(propertyPath.getElements()).containsSequence(
				RootElement.getInstance(),
				new NamedPropertyElement("foo"),
				new NamedPropertyElement("bar")
		);
	}

	@Test
	public void testWithMapKey()
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withRoot()
													  .withMapKey("foo")
													  .build();
		assertThat(propertyPath.getElements()).containsSequence(RootElement.getInstance(), new MapElement("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testWithMapKey_throws_exception_when_key_is_null()
	{
		PropertyPath.createBuilder().withRoot().withMapKey(null).build();
	}

	@Test
	public void testWithCollectionItem()
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withRoot()
													  .withCollectionItem("foo")
													  .build();
		assertThat(propertyPath.getElements()).containsSequence(RootElement.getInstance(), new CollectionElement("foo"));
	}

	@Test
	public void testWithPropertyPath()
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder()
													  .withPropertyPath(PropertyPath
															  .buildWith("foo"))
													  .build();
		assertThat(propertyPath.getElements()).containsSequence(RootElement.getInstance(), new NamedPropertyElement("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testWithPropertyPath_throws_exception_when_property_path_is_null()
	{
		PropertyPath.createBuilder().withPropertyPath(null).build();
	}

	@Test
	public void testBuild_with_one_root_element_should_succeed() throws Exception
	{
		final PropertyPath propertyPath = PropertyPath.createBuilder().withRoot().build();
		assertThat(propertyPath.getElements()).containsOnly(RootElement.getInstance());
	}
}
