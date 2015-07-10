package de.danielbechler.diff.comparison;

/**
 * Allows to configure the way objects identities are established when comparing
 * collections by CollectionDiffer.
 */
public interface IdentityStrategy
{

	/**
	 * @param working never null
	 * @param base
	 * @return
	 */
	// TODO Idea: change name to a less overloaded term
	boolean equals(Object working, Object base);

}
