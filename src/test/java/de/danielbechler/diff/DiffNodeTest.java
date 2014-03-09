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

import de.danielbechler.diff.bean.BeanPropertyAccessor;
import de.danielbechler.diff.bean.BeanPropertyElement;
import de.danielbechler.diff.collection.CollectionItemAccessor;
import de.danielbechler.diff.collection.CollectionItemElement;
import de.danielbechler.diff.helper.NodeAssertions;
import de.danielbechler.diff.mock.ObjectDiffTest;
import org.fest.assertions.api.Assertions;
import org.hamcrest.core.Is;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.Mock;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * @author Daniel Bechler
 */
public class DiffNodeTest
{
	@Mock
	private Accessor accessor;

	@BeforeMethod
	public void setUp()
	{
		initMocks(this);
	}

	@Test
	public void hasChanges_returns_false_when_untouched()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.UNTOUCHED);
		assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void hasChanges_returns_false_when_ignored()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.IGNORED);
		assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void hasChanges_returns_false_when_circular()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.CIRCULAR);
		assertThat(node.hasChanges(), Is.is(false));
	}

	@Test
	public void hasChanges_returns_true_when_changed()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.CHANGED);
		assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void hasChanges_returns_true_when_removed()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.REMOVED);
		assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void hasChanges_returns_true_when_added()
	{
		final DiffNode node = new DiffNode(String.class);
		node.setState(DiffNode.State.ADDED);
		assertThat(node.hasChanges(), Is.is(true));
	}

	@Test
	public void hasChanges_returns_true_when_child_has_changed()
	{
		final DiffNode root = new DiffNode(List.class);
		final DiffNode child = new DiffNode(root, new CollectionItemAccessor("foo"), String.class);
		root.addChild(child);
		child.setState(DiffNode.State.ADDED);
		assertThat(root.hasChanges(), Is.is(true));
	}

	@Test
	public void getPropertyPath_with_parent_node_should_return_canonical_path()
	{
		final DiffNode parentNode = new DiffNode(RootAccessor.getInstance(), String.class);
		when(accessor.getPathElement()).thenReturn(new BeanPropertyElement("foo"));

		final DiffNode root = new DiffNode(parentNode, accessor, Object.class);

		assertThat(root.getPath()).isEqualTo(NodePath.buildWith("foo"));
	}

	@Test
	public void getPropertyPath_without_parent_node_should_return_root_path()
	{
		final DiffNode root = new DiffNode(Object.class);
		assertThat(root.getPath()).isEqualTo(NodePath
				.createBuilder().withRoot().build());
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addChild_throws_exception_when_root_node_is_passed() throws Exception
	{
		final DiffNode root = new DiffNode(Object.class);
		root.addChild(new DiffNode(Object.class));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addChild_throws_exception_when_passed_node_is_already_child_of_another_node() throws Exception
	{
		final DiffNode node1 = new DiffNode(Object.class);
		final DiffNode node2 = new DiffNode(Object.class);
		final DiffNode node3 = new DiffNode(accessor, Object.class);
		node1.addChild(node3);
		node2.addChild(node3);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void addChild_throws_exception_when_node_is_added_to_itself() throws Exception
	{
		final DiffNode node = new DiffNode(null, accessor, Object.class);
		node.addChild(node);
	}

	@Test
	public void addChild_establishes_parent_child_relationship() throws Exception
	{
		final DiffNode node1 = new DiffNode(List.class);
		final DiffNode node2 = new DiffNode(new CollectionItemAccessor("foo"), String.class);
		node1.addChild(node2);
		assertThat(node1.childCount()).isEqualTo(1);
		assertThat(node1.getChild(new CollectionItemElement("foo"))).isSameAs(node2);
		assertThat(node2.getParentNode()).isSameAs(node1);
	}

	@Test
	public void addChild_changes_node_state_to_changed_if_changed_child_node_gets_added()
	{
		final DiffNode node = new DiffNode(Object.class);
		final DiffNode childNode = new DiffNode(node, accessor, String.class);
		childNode.setState(DiffNode.State.CHANGED);

		node.addChild(childNode);

		NodeAssertions.assertThat(node).root().hasState(DiffNode.State.CHANGED);
	}

	@Test
	public void should_return_property_annotations_of_property_accessor() throws Exception
	{
		final BeanPropertyAccessor propertyAccessor = mock(BeanPropertyAccessor.class);
		final Annotation annotation = mock(Annotation.class);
		when(propertyAccessor.getReadMethodAnnotations()).thenReturn(new LinkedHashSet<Annotation>(Arrays.asList(annotation)));
		final DiffNode node = new DiffNode(propertyAccessor, Object.class);

		final Set<Annotation> annotations = node.getPropertyAnnotations();

		Assertions.assertThat(annotations).containsAll(Arrays.asList(annotation));
	}

	@Test
	public void should_return_empty_set_of_property_annotations_if_accessor_is_not_property_accessor() throws Exception
	{
		final BeanPropertyAccessor propertyAccessor = mock(BeanPropertyAccessor.class);
		final Annotation annotation = mock(Annotation.class);
		when(propertyAccessor.getReadMethodAnnotations()).thenReturn(new LinkedHashSet<Annotation>(Arrays.asList(annotation)));
		final DiffNode node = new DiffNode(propertyAccessor, Object.class);

		final Set<Annotation> annotations = node.getPropertyAnnotations();

		Assertions.assertThat(annotations).containsAll(Arrays.asList(annotation));
	}

	@Test
	public void getPropertyAnnotation_should_delegate_call_to_property_accessor()
	{
		final BeanPropertyAccessor propertyAccessor = mock(BeanPropertyAccessor.class);
		when(propertyAccessor.getReadMethodAnnotation(ObjectDiffTest.class)).thenReturn(null);

		new DiffNode(propertyAccessor, Object.class).getPropertyAnnotation(ObjectDiffTest.class);

		verify(propertyAccessor, times(1)).getReadMethodAnnotation(ObjectDiffTest.class);
	}

	@Test
	public void getPropertyAnnotation_should_return_null_if_accessor_is_not_property_accessor()
	{
		final Accessor propertyAccessor = mock(Accessor.class);

		final ObjectDiffTest annotation = new DiffNode(propertyAccessor, Object.class).getPropertyAnnotation(ObjectDiffTest.class);

		assertThat(annotation).isNull();
	}

	@Test
	public void getPropertyName_returns_name_from_PropertyAwareAccessor()
	{
		final String expectedPropertyName = "foo";
		final PropertyAwareAccessor accessor = mock(PropertyAwareAccessor.class);
		when(accessor.getPropertyName()).thenReturn(expectedPropertyName);

		final DiffNode diffNode = new DiffNode(accessor, Object.class);
		final String actualPropertyName = diffNode.getPropertyName();

		assertThat(actualPropertyName).isEqualTo(expectedPropertyName);
	}

	@Test
	public void getPropertyName_returns_name_from_parentNode()
	{
		final String expectedPropertyName = "foo";

		final PropertyAwareAccessor propertyAwareAccessor = mock(PropertyAwareAccessor.class);
		when(propertyAwareAccessor.getPropertyName()).thenReturn(expectedPropertyName);

		final DiffNode parentNodeWithPropertyAwareAccessor = new DiffNode(propertyAwareAccessor, Object.class);
		final DiffNode node = new DiffNode(parentNodeWithPropertyAwareAccessor, mock(Accessor.class), Object.class);

		assertThat(node.getPropertyName()).isEqualTo(expectedPropertyName);
	}

	@Test
	public void getPropertyName_returns_null_when_property_name_can_not_be_resolved()
	{
		final DiffNode node = new DiffNode(mock(Accessor.class), Object.class);

		assertThat(node.getPropertyName()).isNull();
	}

	@Test
	public void isPropertyAware_returns_true()
	{
		final PropertyAwareAccessor propertyAwareAccessor = mock(PropertyAwareAccessor.class);
		final DiffNode node = new DiffNode(propertyAwareAccessor, Object.class);

		assertThat(node.isPropertyAware()).isTrue();
	}

	@Test
	public void isPropertyAware_returns_false()
	{
		final Accessor notAPropertyAwareAccessor = mock(Accessor.class);

		final DiffNode node = new DiffNode(notAPropertyAwareAccessor, Object.class);

		assertThat(node.isPropertyAware()).isFalse();
	}
}
