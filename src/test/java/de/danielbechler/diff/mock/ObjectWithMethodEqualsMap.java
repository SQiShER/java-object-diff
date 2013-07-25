package de.danielbechler.diff.mock;

import java.util.Map;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsMap extends ObjectWithMap {
	public ObjectWithMethodEqualsMap(Map<String, String> map){
		super(map);
	}
	
	@ObjectDiffProperty(methodEqual = "size")
	@Override
	public Map<String, String> getMap()
	{
		return super.getMap();
	}
}
