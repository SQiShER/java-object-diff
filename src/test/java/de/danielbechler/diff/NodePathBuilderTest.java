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

package de.danielbechler.diff;

import de.danielbechler.diff.bean.BeanPropertyElementSelector;
import de.danielbechler.diff.collection.CollectionItemElementSelector;
import de.danielbechler.diff.map.MapKeyElementSelector;
import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * @author Daniel Bechler
 */
public class NodePathBuilderTest
{
	@Test
	public void testWithRoot()
	{
		final NodePath nodePath = NodePath.startBuilding().build();
		assertThat(nodePath.getElementSelectors()).containsOnly(RootElementSelector.getInstance());
	}

	@Test
	public void testWithElement()
	{
		final CollectionItemElementSelector element = new CollectionItemElementSelector("foo");
		final NodePath nodePath = NodePath.startBuilding().element(element).build();
		assertThat(nodePath.getElementSelectors()).containsSequence(
				RootElementSelector.getInstance(),
				element);
	}

	@Test
	public void testWithPropertyName()
	{
		final NodePath nodePath = NodePath.startBuilding().propertyName("foo", "bar").build();
		assertThat(nodePath.getElementSelectors()).containsSequence(
				RootElementSelector.getInstance(),
				new BeanPropertyElementSelector("foo"),
				new BeanPropertyElementSelector("bar")
		);
	}

	@Test
	public void testWithMapKey()
	{
		final NodePath nodePath = NodePath.startBuilding().mapKey("foo").build();
		assertThat(nodePath.getElementSelectors()).containsSequence(RootElementSelector.getInstance(), new MapKeyElementSelector("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testWithMapKey_throws_exception_when_key_is_null()
	{
		NodePath.startBuilding().mapKey(null).build();
	}

	@Test
	public void testWithCollectionItem()
	{
		final NodePath nodePath = NodePath.startBuilding().collectionItem("foo").build();
		assertThat(nodePath.getElementSelectors()).containsSequence(RootElementSelector.getInstance(), new CollectionItemElementSelector("foo"));
	}

	@Test
	public void testWithPropertyPath()
	{
		final NodePath nodePath = NodePath.startBuildingFrom(NodePath.with("foo")).build();
		assertThat(nodePath.getElementSelectors()).containsSequence(RootElementSelector.getInstance(), new BeanPropertyElementSelector("foo"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testWithPropertyPath_throws_exception_when_property_path_is_null()
	{
		NodePath.startBuildingFrom(null).build();
	}

	@Test
	public void testBuild_with_one_root_element_should_succeed() throws Exception
	{
		final NodePath nodePath = NodePath.withRoot();
		assertThat(nodePath.getElementSelectors()).containsOnly(RootElementSelector.getInstance());
	}
}
