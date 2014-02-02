package de.danielbechler.diff;

/**
 * @author Daniel Bechler
 */
public interface InclusionConfiguration
{
	To toInclude();

	To toExclude();

	public interface To
	{
		To categories(String... categories);

		To types(Class<?>... types);

		To nodes(NodePath... nodePath);

		To propertyNames(String... propertyNames);
	}
}
