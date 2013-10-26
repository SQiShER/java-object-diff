package de.danielbechler.diff.issues.issue70;

import java.util.*;

/** @author Daniel Bechler */
public class Person
{
	private String name;
	private List<String> aliases;

	public Person()
	{
	}

	public Person(final String name, final List<String> aliases)
	{
		this.name = name;
		this.aliases = aliases;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(final String name)
	{
		this.name = name;
	}

	public List<String> getAliases()
	{
		return this.aliases;
	}

	public void setAliases(final List<String> aliases)
	{
		this.aliases = aliases;
	}
}
