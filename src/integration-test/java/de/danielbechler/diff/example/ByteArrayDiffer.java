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

package de.danielbechler.diff.example;

import de.danielbechler.diff.NodeQueryService;
import de.danielbechler.diff.access.Instances;
import de.danielbechler.diff.differ.Differ;
import de.danielbechler.diff.differ.DifferDispatcher;
import de.danielbechler.diff.differ.DifferFactory;
import de.danielbechler.diff.node.DiffNode;

import java.util.Arrays;

public class ByteArrayDiffer implements Differ
{
	public boolean accepts(final Class<?> type)
	{
		return type == byte[].class;
	}

	public DiffNode compare(final DiffNode parentNode, final Instances instances)
	{
		final DiffNode node = new DiffNode(parentNode, instances.getSourceAccessor());
		if (instances.hasBeenAdded())
		{
			node.setState(DiffNode.State.ADDED);
		}
		else if (instances.hasBeenRemoved())
		{
			node.setState(DiffNode.State.REMOVED);
		}
		else
		{
			final byte[] baseValue = instances.getBase(byte[].class);
			final byte[] workingValue = instances.getWorking(byte[].class);
			if (!Arrays.equals(baseValue, workingValue))
			{
				node.setState(DiffNode.State.CHANGED);
			}
		}
		return node;
	}

	public static class Factory implements DifferFactory
	{
		public Differ createDiffer(final DifferDispatcher differDispatcher,
								   final NodeQueryService nodeQueryService)
		{
			return new ByteArrayDiffer();
		}
	}
}
