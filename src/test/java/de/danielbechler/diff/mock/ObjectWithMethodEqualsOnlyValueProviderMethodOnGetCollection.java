package de.danielbechler.diff.mock;

import de.danielbechler.diff.introspection.ObjectDiffProperty;

import java.util.Collection;

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
