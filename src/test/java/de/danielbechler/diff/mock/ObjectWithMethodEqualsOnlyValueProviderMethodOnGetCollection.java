package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.*;

import java.util.*;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection extends ObjectWithCollection
{
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(final Collection<String> collection)
	{
		super(collection);
	}

	@ObjectDiffProperty(equalsOnly = true, equalsOnlyValueProviderMethod = "size")
	@Override
	public Collection<String> getCollection()
	{
		return super.getCollection();
	}
}
