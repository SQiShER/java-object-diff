package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject extends ObjectWithNestedObject {
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject(String id){
		super(id);
	}
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetNestedObject(String id, ObjectWithNestedObject object){
		super(id);
		setObject(object);
	}

	@ObjectDiffProperty(equalsOnlyValueProviderMethod = "getId")
	@Override
	public ObjectWithNestedObject getObject() {
		return super.getObject();
	}
}
