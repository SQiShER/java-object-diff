package de.danielbechler.diff;

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
