package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface ExclusionAwareAccessor extends Accessor
{
	boolean isExcluded();
}
