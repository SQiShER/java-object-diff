package de.danielbechler.diff.util;

import de.danielbechler.diff.node.*;
import de.danielbechler.diff.path.*;
import de.danielbechler.diff.visitor.*;
import de.danielbechler.util.*;

import static org.junit.Assert.*;

/** @author Daniel Bechler */
@Deprecated
public abstract class AssertiveSelectorPathVisitor extends AbstractPropertySelectorPathVisitor
{
	private static final Object SKIP = new Object();

	private final Object base;

	private final Object modified;

	private String propertyName;

	private Object baseValue = SKIP;

	private Object modifiedValue = SKIP;

	private DifferenceType differenceType;

	private boolean found;

	private boolean configured;

	public AssertiveSelectorPathVisitor(final Object base, final Object modified)
	{
		Assert.notNull(base, "base");
		Assert.notNull(modified, "modified");
		this.base = base;
		this.modified = modified;
	}

	@Override
	protected void action(final DiffNode<?> match)
	{
		if (!configured)
		{
			configureExpectations();
			configured = true;
		}
		if (propertyName != null)
		{
			assertEquals("PropertyName", propertyName, match.getPropertyName());
		}
		if (differenceType != null)
		{
			assertEquals("DifferenceType", differenceType, match.getType());
		}
		if (baseValue != SKIP)
		{
			assertEquals("Base Value", baseValue, match.getAccessor().get(base));
		}
		if (modifiedValue != SKIP)
		{
			assertEquals("Modified Value", modifiedValue, match.getAccessor().get(modified));
		}
		assertEquals(getSelectorPath(), match.getPropertyPath());
		found = true;
	}

	@Override
	protected abstract void configureSelectorPath(final PropertyPathBuilder builder);

	protected abstract void configureExpectations();

	public boolean isFound()
	{
		return found;
	}

	protected AssertiveSelectorPathVisitor expectedPropertyName(final String propertyName)
	{
		this.propertyName = propertyName;
		return this;
	}

	protected AssertiveSelectorPathVisitor expectedDifferenceType(final DifferenceType differenceType)
	{
		this.differenceType = differenceType;
		return this;
	}

	protected AssertiveSelectorPathVisitor expectedBaseValue(final Object value)
	{
		this.baseValue = value;
		return this;
	}

	protected AssertiveSelectorPathVisitor expectedModifiedValue(final Object value)
	{
		this.modifiedValue = value;
		return this;
	}
}
