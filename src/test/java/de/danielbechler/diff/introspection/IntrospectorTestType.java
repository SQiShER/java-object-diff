package de.danielbechler.diff.introspection;

import java.util.UUID;

public class IntrospectorTestType
{
	public static final String constantField = "constant_field";
	private String privateField;
	String packageProtectedField;
	protected String protectedField;
	@ObjectDiffProperty
	public String publicField;
	public final String publicFinalField = "public_final_field";
	@ObjectDiffProperty(categories = {"field"})
	private String readWriteProperty;
	@ObjectDiffProperty(categories = {"field"})
	private String readOnlyProperty;
	private Number ambiguousSetterProperty;
	public String publicFieldWithAccessors;

	public IntrospectorTestType()
	{
		readOnlyProperty = UUID.randomUUID().toString();
	}

	@ObjectDiffProperty(categories = {"getter"})
	public String getReadWriteProperty()
	{
		return readWriteProperty;
	}

	public void setReadWriteProperty(final String readWriteProperty)
	{
		this.readWriteProperty = readWriteProperty;
	}

	@ObjectDiffProperty(categories = {"getter"})
	public String getReadOnlyProperty()
	{
		return readOnlyProperty;
	}

	public Number getAmbiguousSetterProperty()
	{
		return ambiguousSetterProperty;
	}

	public void setAmbiguousSetterProperty(final Integer ambiguousSetterProperty)
	{
		throw new UnsupportedOperationException();
	}

	public void setAmbiguousSetterProperty(final Number ambiguousSetterProperty)
	{
		this.ambiguousSetterProperty = ambiguousSetterProperty;
	}

	public String getPublicFieldWithAccessors()
	{
		return publicFieldWithAccessors;
	}

	public void setPublicFieldWithAccessors(final String publicFieldWithAccessors)
	{
		this.publicFieldWithAccessors = publicFieldWithAccessors;
	}
}
