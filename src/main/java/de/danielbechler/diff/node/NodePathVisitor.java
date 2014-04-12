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

package de.danielbechler.diff.node;

import de.danielbechler.diff.path.NodePath;
import de.danielbechler.util.Assert;

/**
 * @author Daniel Bechler
 */
public class NodePathVisitor implements DiffNode.Visitor
{
	private final NodePath nodePath;

	private DiffNode node;

	public NodePathVisitor(final NodePath nodePath)
	{
		Assert.notNull(nodePath, "nodePath");
		this.nodePath = nodePath;
	}

	public void node(final DiffNode node, final Visit visit)
	{
		final NodePath differencePath = node.getPath();
		if (differencePath.matches(nodePath) || differencePath.isParentOf(nodePath))
		{
			if (differencePath.matches(nodePath))
			{
				this.node = node;
				visit.stop();
			}
		}
		else
		{
			visit.dontGoDeeper();
		}
	}

	public DiffNode getNode()
	{
		return node;
	}
}
