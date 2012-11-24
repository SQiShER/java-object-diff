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

import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.mockito.internal.debugging.*;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.node.NodeAssertions.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

/** @author Daniel Bechler */
public class CollectionDifferShould
{
	private CollectionDiffer differ;
	@Mock private DelegatingObjectDiffer delegatingObjectDiffer;
	private Configuration configuration;

	@BeforeMethod
	public void setUp() throws Exception
	{
		initMocks(this);
		configuration = new Configuration();
		differ = new CollectionDiffer(delegatingObjectDiffer, configuration);
	}

	@AfterMethod
	public void tearDown()
	{
		new MockitoDebuggerImpl().printInvocations(delegatingObjectDiffer);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void fail_if_constructed_without_delegator_and_configuration()
	{
		new CollectionDiffer(null, null);
	}

	@Test
	public void return_untouched_node_if_base_and_working_are_the_same_instance()
	{
		final List<String> working = emptyList();

		final CollectionNode node = differ.compare(working, working);

		assertThat(node).self().hasState(Node.State.UNTOUCHED);
	}

	@Test
	public void return_added_node_if_working_is_not_null_and_base_is_null() throws Exception
	{
		final CollectionNode node = differ.compare(emptyList(), null);

		assertThat(node).self().hasState(Node.State.ADDED);
	}

	@Test
	public void return_ignored_node_if_property_is_ignored() throws Exception
	{
		configuration.withoutProperty(PropertyPath.buildRootPath());

		final CollectionNode node = differ.compare(emptyList(), null);

		assertThat(node).self().hasState(Node.State.IGNORED);
	}

	@Test
	public void return_removed_node_if_working_is_null_and_base_is_not_null() throws Exception
	{
		final CollectionNode node = differ.compare(null, emptyList());

		assertThat(node).self().hasState(Node.State.REMOVED);
	}

	@Test
	public void compare_only_via_equals_if_equals_only_is_enabled()
	{
		final List<?> working = asList(new ObjectWithIdentityAndValue("foo", "ignore"));
		final List<?> base = asList(new ObjectWithIdentityAndValue("foo", "ignore this too"));
		configuration.withEqualsOnlyProperty(PropertyPath.buildRootPath());

		final CollectionNode node = differ.compare(working, base);

		assertThat(node).self().hasState(Node.State.UNTOUCHED);
	}

	@Test
	public void detect_changes_if_equals_only_is_enabled()
	{
		final List<?> working = asList(new ObjectWithIdentityAndValue("foo", "ignore"));
		final List<?> base = asList(new ObjectWithIdentityAndValue("bar", "ignore"));
		configuration.withEqualsOnlyProperty(PropertyPath.buildRootPath());

		final CollectionNode node = differ.compare(working, base);

		assertThat(node).self().hasState(Node.State.CHANGED);
	}

	@Test
	public void delegate_items_of_added_collection_to_delegator()
	{
		final List<String> working = asList("foo");

		doAnswer(new ReturnDefaultNode(Node.State.ADDED))
				.when(delegatingObjectDiffer)
				.delegate(any(CollectionNode.class), any(Instances.class));

		final CollectionNode node = differ.compare(working, null);

		assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.ADDED);
	}

	@Test
	public void delegate_items_of_removed_collection_to_delegator()
	{
		final List<String> base = asList("foo");

		doAnswer(new ReturnDefaultNode(Node.State.REMOVED))
				.when(delegatingObjectDiffer)
				.delegate(any(CollectionNode.class), any(Instances.class));

		final CollectionNode node = differ.compare(null, base);

		assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.REMOVED);
	}

	@Test
	public void delegate_removed_items_to_delegator_on_deep_comparison()
	{
		final List<String> working = emptyList();
		final List<String> base = asList("foo");

		when(delegatingObjectDiffer.delegate(any(CollectionNode.class), any(Instances.class))).then(new ReturnDefaultNode(Node.State.REMOVED));

		final CollectionNode node = differ.compare(working, base);

		assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.REMOVED);
		verify(delegatingObjectDiffer).delegate(any(CollectionNode.class), any(Instances.class));
		verifyNoMoreInteractions(delegatingObjectDiffer);
	}

	@Test
	public void delegate_all_items_to_delegator_on_deep_comparison()
	{
		final List<String> working = asList("foo", "bar");
		final List<String> base = asList("foobar");

		when(delegatingObjectDiffer.delegate(any(CollectionNode.class), any(Instances.class))).then(new ReturnDefaultNode(Node.State.ADDED));

		final CollectionNode node = differ.compare(working, base);

		verify(delegatingObjectDiffer, times(3)).delegate(any(CollectionNode.class), any(Instances.class));
		verifyNoMoreInteractions(delegatingObjectDiffer);

		assertThat(node).child(new CollectionElement("foo")).hasState(Node.State.ADDED);
		assertThat(node).child(new CollectionElement("bar")).hasState(Node.State.ADDED);
		assertThat(node).child(new CollectionElement("foobar")).hasState(Node.State.ADDED);
	}

	@Test(enabled = false, description = "Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.")
	public void testCompareWithListContainingObjectTwiceDetectsIfOneGetsRemoved() throws Exception
	{
		final List<ObjectWithHashCodeAndEquals> base = asList(new ObjectWithHashCodeAndEquals("foo"), new ObjectWithHashCodeAndEquals("foo"));
		final List<ObjectWithHashCodeAndEquals> working = asList(new ObjectWithHashCodeAndEquals("foo"));
		final CollectionNode node = differ.compare(working, base);
		node.visit(new NodeHierarchyVisitor());
		assertThat(node)
				.child(PropertyPath.createBuilder()
								   .withRoot()
								   .withCollectionItem(new ObjectWithHashCodeAndEquals("foo"))
								   .build())
				.hasState(Node.State.REMOVED);
	}

	@Test(enabled = false, description = "Currently this is simply not possible because of the way, the CollectionItemAccessor works. Would be great, to support this.")
	public void testCompareWithListContainingObjectOnceDetectsIfAnotherInstanceOfItGetsAdded() throws Exception
	{
		final List<ObjectWithHashCodeAndEquals> base = asList(new ObjectWithHashCodeAndEquals("foo"));
		final List<ObjectWithHashCodeAndEquals> working = asList(new ObjectWithHashCodeAndEquals("foo"), new ObjectWithHashCodeAndEquals("foo"));
		final CollectionNode node = differ.compare(working, base);
		node.visit(new NodeHierarchyVisitor());
		assertThat(node)
				.child(PropertyPath.createBuilder()
								   .withRoot()
								   .withCollectionItem(new ObjectWithHashCodeAndEquals("foo"))
								   .build())
				.hasState(Node.State.ADDED);
	}
}
