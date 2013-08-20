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
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import org.testng.annotations.*;

import static de.danielbechler.diff.NodeAssertions.*;

/** @author Daniel Bechler */
public class IgnoreIntegrationTest
{
	@Test
	public void verify_that_ignore_rules_with_complex_paths_works_properly() throws Exception
	{
		final ObjectWithCircularReference obj1 = new ObjectWithCircularReference("1");
		final ObjectWithCircularReference obj2 = new ObjectWithCircularReference("2");
		final ObjectWithCircularReference obj3 = new ObjectWithCircularReference("3");
		obj1.setReference(obj2);
		obj2.setReference(obj3);

		final ObjectWithCircularReference modifiedObj1 = new ObjectWithCircularReference("1");
		final ObjectWithCircularReference modifiedObj2 = new ObjectWithCircularReference("2");
		final ObjectWithCircularReference modifiedObj3 = new ObjectWithCircularReference("4");
		modifiedObj1.setReference(modifiedObj2);
		modifiedObj2.setReference(modifiedObj3);

		final NodePath nodePath = NodePath.buildWith("reference", "reference");

		// verify that the node can be found when it's not excluded
		ObjectDiffer objectDiffer = ObjectDifferBuilder.startBuilding().build();
		final DiffNode verification = objectDiffer.compare(obj1, modifiedObj1);
		verification.visit(new PrintingVisitor(obj1, modifiedObj1));
		assertThat(verification).child(nodePath).hasState(DiffNode.State.CHANGED).hasChildren(1);

		// verify that the node can't be found, when it's excluded
		final ObjectDifferBuilder objectDifferBuilder = ObjectDifferBuilder.startBuilding();
		objectDifferBuilder.configure().inclusion().toExclude().nodes(nodePath);
		objectDiffer = objectDifferBuilder.build();
		final DiffNode node = objectDiffer.compare(obj1, modifiedObj1);
		node.visit(new PrintingVisitor(obj1, modifiedObj1));
		assertThat(node).child(nodePath).doesNotExist();
	}
}
