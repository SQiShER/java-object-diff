package de.danielbechler.diff.accessor;

import de.danielbechler.diff.comparison.*;

/** @author Daniel Bechler */
public interface ComparisonStrategyAwareAccessor extends Accessor
{
	ComparisonStrategy getComparisonStrategy();
}
