package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface ComparisonConfiguration
{
	public interface Of
	{
		ComparisonConfiguration toUse(ComparisonStrategy comparisonStrategy);

		ComparisonConfiguration toUseEqualsMethod();

		ComparisonConfiguration toUseEqualsMethodOfValueProvidedByMethod(String propertyName);

		ComparisonConfiguration toUseCompareToMethod();
	}

	public interface OfPrimitiveTypes
	{
		ComparisonConfiguration toTreatDefaultValuesAs(PrimitiveDefaultValueMode primitiveDefaultValueMode);
	}

	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	OfPrimitiveTypes ofPrimitiveTypes();
}
