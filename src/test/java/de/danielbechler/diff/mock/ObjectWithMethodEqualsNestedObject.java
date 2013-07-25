package de.danielbechler.diff.mock;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsNestedObject extends ObjectWithNestedObject {
	public ObjectWithMethodEqualsNestedObject(String id){
		super(id);
	}
	public ObjectWithMethodEqualsNestedObject(String id, ObjectWithNestedObject object){
		super(id);
		setObject(object);
	}

	@ObjectDiffProperty(methodEqual = "getId")
	@Override
	public ObjectWithNestedObject getObject() {
		return super.getObject();
	}
}
