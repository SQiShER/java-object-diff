package de.danielbechler.diff.mock;

import java.util.*;

/** @author Daniel Bechler */
@Deprecated
public final class TestObject
{
	private int number;
	private String text;
	private Collection<NestableCollectionSafeObject> collection = new ArrayList<NestableCollectionSafeObject>(3);
	private Map<String, NestableCollectionSafeObject> map = new LinkedHashMap<String, NestableCollectionSafeObject>(3);

//		private Map<String, Collection<Item>> experiment = new LinkedHashMap<String, Collection<Item>>(3);

	public String getText()
	{
		return text;
	}

	public void setText(final String text)
	{
		this.text = text;
	}

	public int getNumber()
	{
		return number;
	}

	public void setNumber(final int number)
	{
		this.number = number;
	}

	public Collection<NestableCollectionSafeObject> getCollection()
	{
		return collection;
	}

	public void setCollection(final Collection<NestableCollectionSafeObject> collection)
	{
		this.collection = collection;
	}

	public Map<String, NestableCollectionSafeObject> getMap()
	{
		return map;
	}

	public void setMap(final Map<String, NestableCollectionSafeObject> map)
	{
		this.map = map;
	}

//		public Map<String, Collection<Item>> getExperiment()
//		{
//			return experiment;
//		}
//
//		public void setExperiment(final Map<String, Collection<Item>> experiment)
//		{
//			this.experiment = experiment;
//		}
}
