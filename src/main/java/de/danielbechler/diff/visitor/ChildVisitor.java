package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class ChildVisitor implements Node.Visitor
{
	private final PropertyPath path;

	private Node node;

	public ChildVisitor(final PropertyPath propertyPath)
	{
		Assert.notNull(propertyPath, "propertyPath");
		this.path = propertyPath;
	}

	public void accept(final Node difference, final Visit visit)
	{
		final PropertyPath differencePath = difference.getPropertyPath();
		if (!path.isParentOf(differencePath))
		{
			visit.dontGoDeeper();
			return;
		}
		if (path.equals(differencePath))
		{
			this.node = difference;
			visit.stop();
		}
	}

	public Node getNode()
	{
		return node;
	}
}
