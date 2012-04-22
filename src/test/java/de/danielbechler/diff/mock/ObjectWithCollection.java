package de.danielbechler.diff.mock;

import java.util.*;

/** @author Daniel Bechler */
public class ObjectWithCollection
{
	private Collection<String> collection;

	public ObjectWithCollection()
	{
		this(new LinkedList<String>());
	}

	public ObjectWithCollection(final Collection<String> collection)
	{
		this.collection = collection;
	}

	public Collection<String> getCollection()
	{
		return collection;
	}

	public void setCollection(final Collection<String> collection)
	{
		this.collection = collection;
	}
}
