package de.danielbechler.diff;

/** @author Daniel Bechler */
public interface ComparisonStrategyAwareAccessor extends Accessor
{
	ComparisonStrategy getComparisonStrategy();
}
