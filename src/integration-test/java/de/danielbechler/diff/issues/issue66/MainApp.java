/*
 * Copyright 2013 Daniel Bechler
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

package de.danielbechler.diff.issues.issue66;

import de.danielbechler.diff.DiffNode;
import de.danielbechler.diff.ObjectDiffer;
import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.Visit;

public class MainApp
{
	private MainApp()
	{
	}

	public static void main(final String[] args)
	{
		final ObjectDiffer objectDiffer = ObjectDifferBuilder.buildDefault();

		final TopHat hat1 = new TopHat(1, 10);
		final TopHat hat2 = new TopHat(2, 20);

		final Person p1 = new Person(hat1);
		final Person p2 = new Person(hat2);

		final DiffNode root = objectDiffer.compare(p1, p2);

		root.visit(new DiffNode.Visitor()
		{
			public void accept(final DiffNode node, final Visit visit)
			{
				System.out.print(node.getPath() + " :: ");
				System.out.print(node.canonicalGet(p1));
				System.out.print(" -> ");
				System.out.print(node.canonicalGet(p2));
				System.out.println();
			}
		});
	}
}
