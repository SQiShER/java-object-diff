package de.danielbechler.diff.mock;

import java.util.Collection;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection extends ObjectWithCollection {
	
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetCollection(Collection<String> collection){
		super(collection);
	}
	
	@ObjectDiffProperty(equalsOnlyValueProviderMethod = "size")
	@Override
	public Collection<String> getCollection()
	{
		return super.getCollection();
	}
}
