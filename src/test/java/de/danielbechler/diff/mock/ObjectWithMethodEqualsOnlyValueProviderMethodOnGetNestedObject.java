package de.danielbechler.diff.mock;

import de.danielbechler.diff.config.introspection.ObjectDiffProperty;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject extends ObjectWithNestedObject
{
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject(final String id,
																		  final ObjectWithNestedObject object)
	{
		super(id);
		setObject(object);
	}

	@ObjectDiffProperty(equalsOnly = true, equalsOnlyValueProviderMethod = "getId")
	@Override
	public ObjectWithNestedObject getObject()
	{
		return super.getObject();
	}
}
