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

package de.danielbechler.diff.example;

import de.danielbechler.diff.Visit;
import de.danielbechler.diff.builder.ObjectDifferBuilder;
import de.danielbechler.diff.node.DiffNode;
import de.danielbechler.diff.nodepath.MapKeyElementSelector;

import java.util.Map;
import java.util.TreeMap;

/**
 * @author Daniel Bechler
 */
class MapEntryValueAccessExample
{
	private MapEntryValueAccessExample()
	{
	}

	public static void main(final String[] args)
	{
		final Map<String, String> base = new TreeMap<String, String>();
		final Map<Integer, String> working = new TreeMap<Integer, String>();
		working.put(4, "Locke");
		working.put(8, "Reyes");
		working.put(15, "Ford");
		working.put(16, "Jarrah");
		working.put(23, "Shephard");
		working.put(42, "Kwon");

		final DiffNode mapNode = ObjectDifferBuilder.buildDefault().compare(working, base);
		mapNode.visitChildren(new DiffNode.Visitor()
		{
			public void accept(final DiffNode node, final Visit visit)
			{
				final Object key = ((MapKeyElementSelector) node.getElementSelector()).getKey();
				//                 ^^^ I do not encourage this, but currently it's the only way
				final Object value = node.canonicalGet(working);
				System.out.println(key + " => " + value);
			}
		});
	}
}
