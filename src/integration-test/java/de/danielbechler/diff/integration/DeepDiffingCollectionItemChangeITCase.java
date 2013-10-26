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

package de.danielbechler.diff.integration;

import de.danielbechler.diff.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.visitor.*;
import org.testng.annotations.*;

import java.util.*;

import static de.danielbechler.diff.NodeAssertions.*;

/** @author Daniel Bechler */
public class DeepDiffingCollectionItemChangeITCase
{
	@Test
	public void test_returns_full_property_graph_of_added_collection_items()
	{
		final Map<String, ObjectWithString> base = Collections.emptyMap();
		final Map<String, ObjectWithString> working = Collections.singletonMap("foo", new ObjectWithString("bar"));

		final ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding();
		objectDifferBuilder.configure().introspection().includeChildrenOfNodeWithState(DiffNode.State.ADDED);
		final ObjectDiffer differ = objectDifferBuilder.build();

		final DiffNode node = differ.compare(working, base);

		node.visit(new NodeHierarchyVisitor());

		assertThat(node).child(NodePath.createBuilder()
									   .withRoot()
									   .withMapKey("foo")).hasState(DiffNode.State.ADDED);

		assertThat(node).child(NodePath.createBuilder()
									   .withRoot()
									   .withMapKey("foo")
									   .withPropertyName("value")).hasState(DiffNode.State.ADDED);
	}

	@Test
	public void test_collection_with_null_item()
	{
		ObjectDifferBuilder.buildDefaultObjectDiffer()
						   .compare(Arrays.asList((String) null), Arrays.asList("foobar"));
	}
}
