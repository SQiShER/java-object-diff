package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.*;

/** @author Daniel Bechler */
@ObjectDiffEqualsOnlyType
public class ObjectWithPropertyAnnotations extends ObjectWithHashCodeAndEquals
{
	private String ignored;
	private String equalsOnly;
	private String categorized;

	public ObjectWithPropertyAnnotations(final String key)
	{
		super(key);
	}

	public ObjectWithPropertyAnnotations(final String key, final String value)
	{
		super(key, value);
	}

	@ObjectDiffProperty(ignore = true)
	public String getIgnored()
	{
		return ignored;
	}

	public void setIgnored(final String ignored)
	{
		this.ignored = ignored;
	}

	@ObjectDiffProperty(equalsOnly = true)
	public String getEqualsOnly()
	{
		return equalsOnly;
	}

	public void setEqualsOnly(final String equalsOnly)
	{
		this.equalsOnly = equalsOnly;
	}

	@ObjectDiffProperty(categories = {"foo"})
	public String getCategorized()
	{
		return categorized;
	}

	public void setCategorized(final String categorized)
	{
		this.categorized = categorized;
	}
}
