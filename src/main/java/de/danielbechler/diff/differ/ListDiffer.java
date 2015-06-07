/*
 * Copyright 2015 Daniel Bechler
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

package de.danielbechler.diff.differ;

import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.access.ListItemAccessor;
import de.danielbechler.diff.node.DiffNode;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDiffer implements Differ
{
	public boolean accepts(final Class<?> type)
	{
		return false;
	}

	public DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		final DiffNode node = new DiffNode(parentNode, instances.getSourceAccessor(), instances.getType());
		final List working = instances.getWorking(List.class);
		final List base = instances.getBase(List.class);
		final List<Sequence> sequences = Sequencer.findSequences(working, base);
		System.out.println(sequences);
		final BitSet baseMask = baseMask(base, sequences);
		final Map<Object, Integer> ordinals = new HashMap<Object, Integer>();
		for (int i = 0; i < base.size(); i++)
		{
			final Object item = base.get(i);
			final int ordinal = ordinals.get(item) == null ? 0 : ordinals.get(item) + 1;
			ordinals.put(item, ordinal);
			if (baseMask.get(i))
			{
				final DiffNode itemNode = new DiffNode(node, new ListItemAccessor(item, ordinal), null);
				itemNode.setState(DiffNode.State.UNTOUCHED);
				node.addChild(itemNode);
			}
			else
			{
				final DiffNode itemNode = new DiffNode(node, new ListItemAccessor(item, ordinal), null);
				itemNode.setState(DiffNode.State.REMOVED);
				node.addChild(itemNode);
			}
		}
		ordinals.clear();
		final BitSet workingMask = workingMask(working, sequences);
		for (int i = 0; i < working.size(); i++)
		{
			final Object item = working.get(i);
			final int ordinal = ordinals.get(item) == null ? 0 : ordinals.get(item) + 1;
			ordinals.put(item, ordinal);
			if (!workingMask.get(i))
			{
				final DiffNode itemNode = new DiffNode(node, new ListItemAccessor(item, ordinal), null);
				itemNode.setState(DiffNode.State.ADDED);
				node.addChild(itemNode);
			}
		}
		return node;
	}

	private static BitSet workingMask(final Collection<?> collection, final Iterable<Sequence> sequences)
	{
		final BitSet bitSet = new BitSet(collection.size());
		for (final Sequence sequence : sequences)
		{
			bitSet.set(sequence.getWorkingOffset(), sequence.getWorkingOffset() + sequence.length(), true);
		}
		return bitSet;
	}

	private static BitSet baseMask(final Collection<?> collection, final Iterable<Sequence> sequences)
	{
		final BitSet bitSet = new BitSet(collection.size());
		for (final Sequence sequence : sequences)
		{
			bitSet.set(sequence.getBaseOffset(), sequence.getBaseOffset() + sequence.length(), true);
		}
		return bitSet;
	}
}
