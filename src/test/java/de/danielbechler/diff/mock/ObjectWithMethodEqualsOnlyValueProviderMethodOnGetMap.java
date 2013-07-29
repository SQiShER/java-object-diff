package de.danielbechler.diff.mock;

import java.util.Map;

import de.danielbechler.diff.annotation.ObjectDiffProperty;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap extends ObjectWithMap {
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(Map<String, String> map){
		super(map);
	}
	
	@ObjectDiffProperty(equalsOnlyValueProviderMethod = "size")
	@Override
	public Map<String, String> getMap()
	{
		return super.getMap();
	}
}
