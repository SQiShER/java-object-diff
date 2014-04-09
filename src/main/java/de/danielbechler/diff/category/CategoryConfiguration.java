package de.danielbechler.diff.category;

import de.danielbechler.diff.nodepath.NodePath;

/**
 * Allows to assign custom categories (or tags) to entire types or selected elements and properties. These categories
 * come in very handy, when combined with the `InclusionConfiguration`. They make it very easy to limit the comparison
 * to a specific subset of the object graph.
 *
 * @author Daniel Bechler
 */
public interface CategoryConfiguration
{
	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	public interface Of
	{
		CategoryConfiguration toBe(String... categories);
	}
}
