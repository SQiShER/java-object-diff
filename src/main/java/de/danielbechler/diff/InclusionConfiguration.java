package de.danielbechler.diff;

import de.danielbechler.diff.path.*;

/** @author Daniel Bechler */
public interface InclusionConfiguration
{
	To toInclude();

	To toExclude();

	public interface To
	{
		To categories(String... categories);

		To types(Class<?>... types);

		To nodes(NodePath... nodePath);
	}
}
