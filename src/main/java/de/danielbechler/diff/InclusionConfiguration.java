package de.danielbechler.diff;

/**
 * Allows to in- or exclude nodes based on property name, object type, category or location in the object graph.
 * Excluded nodes will not be compared, to make sure their accessors won't get called. This is useful in cases where
 * getters could throw exceptions under certain conditions or when certain accessors are expensive to call or simply
 * not relevant for the use-case.
 * <p/>
 * In combination with categories this allows to define sub-sets of properties, in order to compare only relevant parts
 * of an object (e.g. exclude all properties marked as _metadata_.)
 *
 * @author Daniel Bechler
 */
public interface InclusionConfiguration
{
	/**
	 * Includes elements (and implicitly all their children) based on certain criteria, unless their parent element
	 * doesn't match any inclusion rules.
	 */
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
