package de.danielbechler.diff.introspection;

@SuppressWarnings("WeakerAccess")
public class DtoForTesting
{
	@ObjectDiffProperty(categories = {"foo"}, excluded = true)
	public String publicField;
	public final String publicFinalField;

	public DtoForTesting(String publicFinalField)
	{
		this.publicFinalField = publicFinalField;
	}
}
