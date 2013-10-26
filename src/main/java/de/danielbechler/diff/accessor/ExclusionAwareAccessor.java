package de.danielbechler.diff.accessor;

/** @author Daniel Bechler */
public interface ExclusionAwareAccessor extends Accessor
{
	boolean isExcluded();
}
