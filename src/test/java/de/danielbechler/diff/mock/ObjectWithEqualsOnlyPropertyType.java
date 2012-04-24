package de.danielbechler.diff.mock;

/** @author Daniel Bechler */
public class ObjectWithEqualsOnlyPropertyType
{
	private ObjectWithPropertyAnnotations child;

	public ObjectWithPropertyAnnotations getChild()
	{
		return child;
	}

	public void setChild(final ObjectWithPropertyAnnotations child)
	{
		this.child = child;
	}
}
