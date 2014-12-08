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

package de.danielbechler.diff.example.gettingstarted;

import de.danielbechler.diff.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.node.Visit;
import de.danielbechler.diff.path.NodePath;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("LocalCanBeFinal")
public class StarterGuide
{
	private StarterGuide()
	{
	}

	public static void main(final String[] args)
	{
		final Map<String, String> working = Collections.singletonMap("item", "foo");
		final Map<String, String> base = Collections.singletonMap("item", "bar");
		DiffNode diff = ObjectDifferBuilder.buildDefault().compare(working, base);

		assert diff.hasChanges();
		assert diff.childCount() == 1;
		NodePath itemPath = NodePath.startBuilding().mapKey("item").build();
		assert diff.getChild(itemPath).getState() == DiffNode.State.CHANGED;

		diff.visit(new DiffNode.Visitor()
		{
			public void node(DiffNode node, Visit visit)
			{
				System.out.println(node.getPath() + " => " + node.getState());
			}
		});

		diff.visit(new DiffNode.Visitor()
		{
			public void node(DiffNode node, Visit visit)
			{
				final Object baseValue = node.canonicalGet(base);
				final Object workingValue = node.canonicalGet(working);
				final String message = node.getPath() + " changed from " +
						baseValue + " to " + workingValue;
				System.out.println(message);
			}
		});

		final Map<String, String> head = new HashMap<String, String>(base);
		head.put("another", "map");
		diff.visit(new DiffNode.Visitor()
		{
			public void node(DiffNode node, Visit visit)
			{
				if (node.hasChanges() && !node.hasChildren())
				{
					node.canonicalSet(head, node.canonicalGet(working));
				}
			}
		});
		assert head.get("item").equals("foo");
		assert head.get("another").equals("map");
	}
}
