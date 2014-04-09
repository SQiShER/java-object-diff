package de.danielbechler.diff.config.comparison;

import de.danielbechler.diff.node.ComparisonStrategy;
import de.danielbechler.diff.node.path.NodePath;

/**
 * Allows to configure the way objects are compared. Sometimes introspection is just not the way to go. Let it be for
 * performance reasons or simply because the object doesn't expose any useful properties. In those cases it's possible
 * to define alternative comparison strategies, like using the equals method, a comparator or even a custom strategy.
 * These settings can be made for specific nodes or entire types.
 *
 * @author Daniel Bechler
 */
public interface ComparisonConfiguration
{
	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	OfPrimitiveTypes ofPrimitiveTypes();

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
}
