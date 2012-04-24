package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
public class PropertyVisitor implements Node.Visitor
{
	private final PropertyPath propertyPath;

	private Node node;

	public PropertyVisitor(final PropertyPath propertyPath)
	{
		Assert.notNull(propertyPath, "propertyPath");
		this.propertyPath = propertyPath;
	}

	public void accept(final Node difference, final Visit visit)
	{
		final PropertyPath differencePath = difference.getPropertyPath();
		if (propertyPath.isParentOf(differencePath))
		{
			if (propertyPath.equals(differencePath))
			{
				this.node = difference;
				visit.stop();
			}
		}
		else
		{
			visit.dontGoDeeper();
		}
	}

	public Node getNode()
	{
		return node;
	}
}
