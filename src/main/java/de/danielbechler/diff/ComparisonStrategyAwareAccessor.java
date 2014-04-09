package de.danielbechler.diff;

import de.danielbechler.diff.node.ComparisonStrategy;

/**
 * @author Daniel Bechler
 */
public interface ComparisonStrategyAwareAccessor extends Accessor
{
	ComparisonStrategy getComparisonStrategy();
}
