package de.danielbechler.diff.config.inclusion;

import de.danielbechler.diff.node.path.NodePath;

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
public interface InclusionConfiguration<T>
{
	/**
	 * Includes elements (and implicitly all their children) based on certain criteria, unless their parent element
	 * doesn't match any inclusion rules.
	 */
	ToInclude<T> include();

	ToExclude<T> exclude();

	public interface ToInclude<T>
	{
		ToIncludeAndReturn<T> category(String category);

		ToIncludeAndReturn<T> type(Class<?> type);

		ToIncludeAndReturn<T> node(NodePath nodePath);

		ToIncludeAndReturn<T> propertyName(String propertyName);

		ToExclude<T> exclude();
	}

	public interface ToIncludeAndReturn<T> extends ToInclude<T>
	{
		T and();
	}

	public interface ToExclude<T>
	{
		ToExcludeAndReturn<T> category(String category);

		ToExcludeAndReturn<T> type(Class<?> type);

		ToExcludeAndReturn<T> node(NodePath nodePath);

		ToExcludeAndReturn<T> propertyName(String property);

		ToInclude<T> include();
	}

	public interface ToExcludeAndReturn<T> extends ToExclude<T>
	{
		T and();
	}
}
