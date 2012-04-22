package de.danielbechler.diff.visitor;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.util.*;

/** @author Daniel Bechler */
public abstract class AbstractPropertySelectorPathVisitor implements DiffNode.Visitor
{
	private final PropertyPathBuilder selectorPathBuilder;

	private PropertyPath selectorPath;

	public AbstractPropertySelectorPathVisitor()
	{
		this.selectorPathBuilder = new PropertyPathBuilder();
	}

	public AbstractPropertySelectorPathVisitor(final PropertyPath selectorPath)
	{
		Assert.notNull(selectorPath, "selectorPath");
		this.selectorPath = selectorPath;
		this.selectorPathBuilder = null;
	}

	public final void accept(final DiffNode<?> difference, final Visit visit)
	{
		if (selectorPath == null)
		{
			configureSelectorPath(selectorPathBuilder);
			selectorPath = selectorPathBuilder.toPropertyPath();
		}
		if (difference.matches(selectorPath))
		{
			action(difference);
			visit.stop();
		}
	}

	protected void configureSelectorPath(final PropertyPathBuilder builder)
	{
	}

	/** Action performed when the property has been found. */
	protected abstract void action(DiffNode<?> match);

	public PropertyPath getSelectorPath()
	{
		return selectorPath;
	}
}
