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

package de.danielbechler.diff.issues.issue15;

import de.danielbechler.diff.PrintingVisitor;
import de.danielbechler.diff.builder.ObjectDiffer;
import de.danielbechler.diff.builder.ObjectDifferBuilder;
import de.danielbechler.diff.mock.ObjectWithCircularReference;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.nodepath.NodePath;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import static de.danielbechler.diff.helper.NodeAssertions.assertThat;

/**
 * @author Daniel Bechler
 */
public class CircularReferenceIT
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

		final DiffNode root = ObjectDifferBuilder.buildDefault().compare(workingA, baseA);
		assertThat(root).child("reference", "reference").isCircular();
		assertThat(root).child("reference", "reference")
				.hasCircularStartPathEqualTo(NodePath.withRoot());

		Assertions.assertThat(root.canonicalGet(workingA))
				.isSameAs(root.getChild("reference").getChild("reference").canonicalGet(workingA));
		Assertions.assertThat(root.getChild("reference").getChild("reference").getCircleStartNode())
				.isSameAs(root);
	}

	@Test
	public void testCircularReferenceShouldBeAddedWhenEnabledInConfiguration()
	{
		final ObjectWithCircularReference workingA = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference workingB = new ObjectWithCircularReference("b");
		final ObjectWithCircularReference workingC = new ObjectWithCircularReference("c");
		workingA.setReference(workingB);
		workingB.setReference(workingC);
		workingC.setReference(workingA);

		final ObjectWithCircularReference baseA = new ObjectWithCircularReference("a");
		final ObjectWithCircularReference baseB = new ObjectWithCircularReference("b");
		final ObjectWithCircularReference baseC = new ObjectWithCircularReference("d");
		baseA.setReference(baseB);
		baseB.setReference(baseC);
		baseC.setReference(baseA);

		final ObjectDiffer objectDiffer = ObjectDifferBuilder.buildDefault();
//		objectDiffer.getConfiguration().withoutCircularNodes();
		final DiffNode root = objectDiffer.compare(workingA, baseA);
		root.visit(new PrintingVisitor(workingA, baseA));
		assertThat(root).child("reference", "reference", "reference").isCircular();
	}
}
