package de.danielbechler.diff;

import de.danielbechler.diff.path.*;

/** @author Daniel Bechler */
public interface CategoryConfiguration
{
	Of ofNode(NodePath nodePath);

	Of ofType(Class<?> type);

	public interface Of
	{
		CategoryConfiguration toBe(String... categories);
	}
}
