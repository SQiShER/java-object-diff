package de.danielbechler.diff.identity;

/**
 * Allows to configure the way objects identities are established when comparing
 * collections via {@linkplain de.danielbechler.diff.differ.CollectionDiffer}.
 */
public interface IdentityStrategy
{
	/**
	 * TODO Contract: {@linkplain IdentityStrategy#equals(Object working, Object base)} must always be <code>true</code>
	 * when <code>working == base</code>
	 *
	 * @param working
	 * @param base
	 * @return
	 */
	boolean equals(Object working, Object base);
}
