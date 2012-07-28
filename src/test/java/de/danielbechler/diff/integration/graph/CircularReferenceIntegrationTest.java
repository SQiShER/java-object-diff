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

package de.danielbechler.diff.integration.graph;

import de.danielbechler.diff.*;
import de.danielbechler.diff.mock.*;
import de.danielbechler.diff.node.*;
import org.junit.*;

import static de.danielbechler.diff.node.NodeAssertions.*;

/** @author Daniel Bechler */
public class CircularReferenceIntegrationTest
{
	@Test
	public void testCircularReference()
	{
		final ObjectWithCircularReference workingA = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference workingB = new ObjectWithCircularReference("b");
		workingA.setReference(workingB);
		workingB.setReference(workingA);

		final ObjectWithCircularReference baseA = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference baseB = new ObjectWithCircularReference("c");
		baseA.setReference(baseB);
		baseB.setReference(baseA);

		final Node root = ObjectDifferFactory.getInstance().compare(workingA, baseA);
		assertThat(root).child("reference", "reference").hasState(Node.State.CIRCULAR);
	}
}
