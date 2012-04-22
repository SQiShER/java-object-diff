package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class ChildVisitor implements DiffNode.Visitor
{
	private final PropertyPath path;

	private DiffNode<?> difference;

	public ChildVisitor(final PropertyPath path)
	{
		Assert.notNull(path, "path");
		this.path = path;
	}

	public void accept(final DiffNode<?> difference, final Visit visit)
	{
		final PropertyPath differencePath = difference.getCanonicalAccessor().getPath();
		if (!path.isParentOf(differencePath))
		{
			visit.dontGoDeeper();
			return;
		}
		if (path.equals(differencePath))
		{
			this.difference = difference;
			visit.stop();
		}
	}

	public DiffNode<?> getDifference()
	{
		return difference;
	}
}
