package de.danielbechler.diff.mock;

import java.util.*;

/** @author Daniel Bechler */
public class ObjectWithMap
{
	private Map<String, String> map;

	public ObjectWithMap()
	{
		this(new TreeMap<String, String>());
	}

	public ObjectWithMap(final Map<String, String> map)
	{
		this.map = map;
	}

	public Map<String, String> getMap()
	{
		return map;
	}

	public void setMap(final Map<String, String> map)
	{
		this.map = map;
	}
}
