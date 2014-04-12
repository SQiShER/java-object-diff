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

package de.danielbechler.diff.issues.issue70;

import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.helper.NodeAssertions;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.PrintingVisitor;
import de.danielbechler.diff.path.NodePath;
import org.testng.annotations.Test;

import java.util.Arrays;

public class PersonDiffIT
{
	@Test
	public void testIncludeCollectionAttribute()
	{
		final Person a = new Person("Gulen Chongtham", Arrays.asList("Hola Espanyol", "Vicky Boss"));
		final Person b = new Person("Gulen Chongthamm", Arrays.asList("Hola Espanyol", "Vicky Boss", "Roger Harper"));

		final ObjectDifferBuilder builder = ObjectDifferBuilder.startBuilding();
		builder.configure().inclusion().include().node(NodePath.with("aliases"));
		final ObjectDiffer differ = builder.build();

		final DiffNode root = differ.compare(b, a);
		root.visit(new PrintingVisitor(b, a));

		NodeAssertions.assertThat(root).root().hasChanges();
	}
}
