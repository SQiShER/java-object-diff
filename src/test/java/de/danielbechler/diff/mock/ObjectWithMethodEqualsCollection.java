package de.danielbechler.diff.mock;

import java.util.Collection;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsCollection extends ObjectWithCollection {
	
	public ObjectWithMethodEqualsCollection(Collection<String> collection){
		super(collection);
	}
	
	@ObjectDiffProperty(methodEqual = "size")
	@Override
	public Collection<String> getCollection()
	{
		return super.getCollection();
	}
}
