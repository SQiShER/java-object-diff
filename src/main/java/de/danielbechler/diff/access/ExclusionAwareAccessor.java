package de.danielbechler.diff.access;

/**
 * @author Daniel Bechler
 */
public interface ExclusionAwareAccessor extends Accessor
{
	boolean isExcluded();
}
