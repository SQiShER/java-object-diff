package de.danielbechler.diff.introspection;

public class IntrospectorTestType
{
	public static final String constantField = "constant_field";
	private String privateField;
	String packageProtectedField;
	protected String protectedField;
	public String publicField;
	@ObjectDiffProperty
	public String annotatedPublicField;
	public final String publicFinalField = "public_final_field";
	private String readWriteProperty;
	@ObjectDiffProperty
	private String fieldAnnotatedReadOnlyProperty;

	@ObjectDiffProperty
	public String getReadWriteProperty()
	{
		return readWriteProperty;
	}

	public void setReadWriteProperty(final String readWriteProperty)
	{
		this.readWriteProperty = readWriteProperty;
	}

	public String getFieldAnnotatedReadOnlyProperty()
	{
		return fieldAnnotatedReadOnlyProperty;
	}
}
