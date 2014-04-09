package de.danielbechler.diff.node;

/** @author Daniel Bechler */
public interface ExclusionAwareAccessor extends Accessor
{
	boolean isExcluded();
}
