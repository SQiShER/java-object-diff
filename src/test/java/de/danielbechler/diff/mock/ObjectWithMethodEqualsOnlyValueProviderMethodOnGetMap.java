package de.danielbechler.diff.mock;

import de.danielbechler.diff.config.introspection.ObjectDiffProperty;

import java.util.Map;

public class ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap extends ObjectWithMap
{
	public ObjectWithMethodEqualsOnlyValueProviderMethodOnGetMap(final Map<String, String> map)
	{
		super(map);
	}

	@ObjectDiffProperty(equalsOnly = true, equalsOnlyValueProviderMethod = "size")
	@Override
	public Map<String, String> getMap()
	{
		return super.getMap();
	}
}
